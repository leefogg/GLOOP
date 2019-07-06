package GLOOP.graphics.rendering;

import GLOOP.graphics.Settings;
import GLOOP.graphics.data.models.Decal;
import GLOOP.graphics.data.models.Model;
import GLOOP.graphics.data.models.Model2D;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.lights.DirectionalLight;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.lights.SpotLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.shading.posteffects.PostProcessor;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glDisablei;
import static org.lwjgl.opengl.GL30.glEnablei;

public class DeferredRenderer extends Renderer {
	private static final Vector3f passthrough = new Vector3f();
	private boolean isDisposed;
	private FrameBuffer GBuffers, targetFBO;
	private Texture
			resolveTexture,
			albedoTexture,
			normalsTexture,
			positionTexture,
			specularTexture,
			lightTexture;
	private static DeferredGBuffersShader GBuffersShader;
	private GBufferDeferredLightingPassShader lightingshader;
	private GBufferLightingPassPostEffect lightingPosteffect;
	private PointLightGBufferPostEffect pointlightLightingPost;
	private SpotLightGBufferPostEffect spotLightLightingPost;
	private AmbientLightGBufferPostEffect ambientLightPostEffect;
	private DirectionalLightGBufferPostEffect directionalLightPostEffect;
	private DitherGBufferPostEffect ditherPostEffect;
	private FogGBufferPostEffect fogPostEffect;
	private boolean HDREnabled;
	private ArrayList<GBufferPostEffect> PostEffects = new ArrayList<>();

	private int debugGBufferColorIndex = 0;

	DeferredRenderer() throws IOException, ShaderCompilationException {
		load();
	}

	private void load() throws IOException {
		System.out.println("Starting up deferred rendering pipeline.");

		setHDREnabled();

		PixelFormat precisionrange = HDREnabled ? PixelFormat.RGB16F : PixelFormat.RGB8;
		PixelFormat[] buffers = new PixelFormat[]{
				precisionrange,
				PixelFormat.RGB8, // Specularity, roughness and stencil
				PixelFormat.RGB8, // Normals in world space
				PixelFormat.RGBA16F, // World-space XYZ and depth in W
				precisionrange // Lighting buffer
		};
		GBuffers = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), buffers, true, true);

		// Save for deferred shading pass
		FrameBufferColorTexture[] attachments = GBuffers.getAllColorAttachments();
		albedoTexture = attachments[0];
		specularTexture = attachments[1];
		normalsTexture = attachments[2];
		positionTexture = attachments[3];
		lightTexture = attachments[4];

		loadShaders();

		isDisposed = false;
	}

	private void loadShaders() throws IOException {
		Map<String, String> defines = new HashMap<>();
		
		if (GBuffersShader == null || GBuffersShader.isDisposed()) {
			if (Settings.EnableSpecularMapping)
				defines.put("SPECULARMAPPING", "");
			if (Settings.EnableNormalMapping)
				defines.put("NORMALMAPPING", "");
			if (Settings.EnableEnvironemntMapping)
				defines.put("ENVIRONMENTMAP", "");
			if (Settings.EnableReflectivity)
				defines.put("REFLECTIVITY", "");
			if (Settings.EnableRefractivity)
				defines.put("REFRACTIVITY", "");
			if (Settings.EnableChromaticAberration)
				defines.put("CHROMATICABERRATION", "");
			if (Settings.EnableParallaxMapping)
				defines.put("PARALLAXMAPPING", "");
			if (Settings.EnableFresnel)
				defines.put("FRESNEL", "");

			GBuffersShader = new DeferredGBuffersShader(defines.entrySet());
		}
		if (lightingshader == null || lightingshader.isDisposed()) {
			defines.clear();
			if (Settings.EnableDither)
				defines.put("DITHER", "");
			if (Settings.EnableFog)
				defines.put("FOG", "");
			if (Settings.EnableVolumetricLights)
				defines.put("VOLUMETRICLIGHTING", "");

			defines.put("MAX_POINT_LIGHTS", Integer.toString(Settings.MaxPointLights));
			defines.put("MAX_SPOT_LIGHTS", Integer.toString(Settings.MaxSpotLights));
			defines.put("MAX_DIRECTIONAL_LIGHTS", Integer.toString(Settings.MaxDirectionalLights));

			lightingshader = new GBufferDeferredLightingPassShader(defines.entrySet());
			lightingPosteffect = new GBufferLightingPassPostEffect(lightingshader, normalsTexture, specularTexture, positionTexture);

			PointLightDeferredLightingPassShader pointlightshader = new PointLightDeferredLightingPassShader();
			pointlightLightingPost = new PointLightGBufferPostEffect(pointlightshader, normalsTexture, specularTexture, positionTexture);

			SpotLightDeferredLightingPassShader spotlightshader = new SpotLightDeferredLightingPassShader();
			spotLightLightingPost = new SpotLightGBufferPostEffect(spotlightshader, normalsTexture, specularTexture, positionTexture);

			AmbientLightDeferredLightingPassShader ambientlightShader = new AmbientLightDeferredLightingPassShader();
			ambientLightPostEffect = new AmbientLightGBufferPostEffect(ambientlightShader, normalsTexture, specularTexture, positionTexture);

			DirectionalLightLightingPassShader directionallightShader = new DirectionalLightLightingPassShader();
			directionalLightPostEffect = new DirectionalLightGBufferPostEffect(directionallightShader, normalsTexture, specularTexture, positionTexture);

			DitherDeferredLightingPassShader ditherShader = new DitherDeferredLightingPassShader();
			ditherPostEffect = new DitherGBufferPostEffect(ditherShader, normalsTexture, specularTexture, positionTexture);

			FogDeferredLightingPassShader fogShader = new FogDeferredLightingPassShader();
			fogPostEffect = new FogGBufferPostEffect(fogShader, normalsTexture, specularTexture, positionTexture);
		}

		Renderer.checkErrors();
	}

	static DeferredGBuffersShader getGBuffersShader() {
		return GBuffersShader;
	}

	public void setHDREnabled() {
		// If different setting
		if (targetFBO != null)
			if (Settings.EnableHDR == HDREnabled)
				return;

		HDREnabled = Settings.EnableHDR;

		// Delete
		if (targetFBO != null)
			targetFBO.dispose();

		// Recreate
		targetFBO = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), HDREnabled ? PixelFormat.RGB16F : PixelFormat.RGB16);

		// Store
		resolveTexture = targetFBO.getColorTexture(0);
	}

	@Override
	public void bind(Renderer previoustechnique) {
		GBuffers.bind();
	}

	@Override
	public void reset() {
		// Always fully writes over buffers so no need to clear
	}

	private void renderModels(boolean transparrent) {
		for (Model3D model : scene.getModels()) {
			if (!model.getMaterial().usesDeferredPipeline())
				continue;
			if (model.visibility() == Model.Visibility.NotVisible)
				continue;
			if (model.getMaterial().isTransparent() != transparrent)
				continue;

			model.render();
		}
	}

	@Override
	protected void renderScene() {
		final IntBuffer enabledrenderattachments = GBuffers.getEnabledColorAttachments();

		clear(true, true, false);

		renderModels(false);
		glEnablei(GL_BLEND, 0);
		setBlendFunctionsState(BlendFunction.SourceAlpha, BlendFunction.OneMinusSourceAlpha);
		renderModels(true);
		popBlendFunctionsState();
		glDisablei(GL_BLEND, 0);


		for (Decal decal : scene.getDecals())
			decal.render();

		GBuffers.enableRenderAttachments(enabledrenderattachments);

		//TODO: Move to first thing for efficiency
		for (Model2D overlay : scene.getOverlays())
			if (overlay.getMaterial().usesDeferredPipeline())
				overlay.render();
	}

	@Override
	public Texture getTexture() {
		shade();

		return resolveTexture;
	}

	@Override
	public FrameBuffer getBuffer() {
		shade();

		return targetFBO;
	}

	private void shade() {
		if (isDisposed)
			return;

		// Render lights
		GBuffers.bind();
		enableBlending(true);
		setBlendFunctionsState(BlendFunction.One, BlendFunction.One); // Additive

		RenderSimpleLights();
		RenderComplexLights();
		scene.getAmbientlight().getColor(passthrough);
		ambientLightPostEffect.setAmbientColor(passthrough);
		ambientLightPostEffect.render();
		if (Settings.EnableDither)
			ditherPostEffect.render();
		popBlendFunctionsState();
		setBlendFunctionsState(BlendFunction.OneMinusSourceAlpha, BlendFunction.SourceAlpha); // Mix
		if (Settings.EnableFog) {
			scene.getFogColor(passthrough);
			fogPostEffect.setFogColor(passthrough);
			fogPostEffect.setFogDensity(scene.getFogDensity());
			fogPostEffect.render();
		}
		popBlendFunctionsState();

		setBlendFunctionsState(BlendFunction.DestinationColor, BlendFunction.Zero); // Multiplicative
		for (GBufferPostEffect posteffect : PostEffects)
			posteffect.render();

		// Blend lights with albedo texture attachment
		targetFBO.bind();
		GBuffers.blitTo(targetFBO, true, true, false); //TODO: Check stencil is making its way from the GBuffer to forward renderer
		PostProcessor.render(lightTexture);
		popBlendFunctionsState();
		popBlendingEnabledState();
	}

	private void RenderComplexLights() {
		for (int i=0; i<scene.getNumberOfPointLights(); i++) {
			PointLight light = scene.getPointLight(i);
			if (light.IsComplex()) {
				pointlightLightingPost.set(light);
				pointlightLightingPost.render();
			}
		}
		for (int i=0; i<scene.getNumberOfSpotLights(); i++) {
			SpotLight light = scene.getSpotLight(i);
			if (light.IsComplex()) {
				spotLightLightingPost.set(light);
				spotLightLightingPost.render();
			}
		}
		for (int i=0; i<scene.getNumberOfDirectionalLights(); i++) {
			DirectionalLight light = scene.getDirectionallight(i);
			if (light.IsComplex()) {
				directionalLightPostEffect.set(scene.getDirectionallight(i));
				directionalLightPostEffect.render();
			}
		}
	}

	private void RenderSimpleLights() {
		List<PointLight> pointLights = new ArrayList<>(Settings.MaxPointLights);
		List<SpotLight> spotLights = new ArrayList<>(Settings.MaxSpotLights);
		List<DirectionalLight> directionalLights = new ArrayList<>(Settings.MaxDirectionalLights);
		int
			pli = 0,
			sli = 0,
			dli = 0;
		do {
			// Load up the next batch
			for (; pli<scene.getNumberOfPointLights() && pointLights.size() < Settings.MaxPointLights; pli++) {
				PointLight light = scene.getPointLight(pli);
				if (!light.IsComplex())
					pointLights.add(light);
			}
			for (; sli<scene.getNumberOfSpotLights() && spotLights.size() < Settings.MaxSpotLights; sli++) {
				SpotLight light = scene.getSpotLight(sli);
				if (!light.IsComplex())
					spotLights.add(light);
			}
			for (; dli<scene.getNumberOfDirectionalLights() && directionalLights.size() < Settings.MaxDirectionalLights; dli++) {
				DirectionalLight light = scene.getDirectionallight(dli);
				if (!light.IsComplex())
					directionalLights.add(light);
			}

			lightingPosteffect.setDirectionalLights(directionalLights);
			lightingPosteffect.setPointLights(pointLights);
			lightingPosteffect.setSpotLights(spotLights);
			PostProcessor.render(lightingPosteffect);

			pointLights.clear();
			spotLights.clear();
			directionalLights.clear();
		} while (pli < scene.getNumberOfPointLights() || sli < scene.getNumberOfSpotLights() || dli < scene.getNumberOfDirectionalLights());
	}

	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	public void dispose() {
		if (isDisposed)
			return;

		GBuffers.dispose();
		GBuffers = null;
		targetFBO.dispose();
		targetFBO = null;
		lightingshader.requestDisposal();

		isDisposed = true;
	}

	public DeferredMaterial getNewMaterial() { return new DeferredMaterial(); }

	public void addPostEffect(GBufferPostEffect posteffect) {
		posteffect.setNormalTexture(normalsTexture);
		posteffect.setPositionTexture(positionTexture);
		posteffect.setSpecularTexture(specularTexture);
		PostEffects.add(posteffect);
	}

	public void setVolumetricLightsStrength(float volumetriclightsstrength) {
		lightingPosteffect.setVolumetricLightsStrength(volumetriclightsstrength);
		spotLightLightingPost.setVolumetricLightsStrength(volumetriclightsstrength);
	}

	public void debugGBuffer() {
		// Update which buffer
		int mousescroll = Math.max(-1, Math.min(Mouse.getDWheel(), 1));
		debugGBufferColorIndex += mousescroll;
		int numbuffers = GBuffers.getNumberOfColorAttachments() + 1;
		if (debugGBufferColorIndex == numbuffers)
			debugGBufferColorIndex = 0;
		else if (debugGBufferColorIndex == -1)
			debugGBufferColorIndex = numbuffers-1;

		// Dont show any overlay if on 0. 0 Is nothing/output/normal
		if (debugGBufferColorIndex == 0)
			return;

		PostProcessor.render(GBuffers.getColorTexture(debugGBufferColorIndex -1));
	}

	public void reload() throws IOException {
		// TODO: reload GBuffers and update HDREnabled
		glFlush();
		glFinish();
		GBuffersShader.dispose();
		lightingshader.dispose();

		loadShaders();
	}

	public Texture getAlbedoTexture() { return albedoTexture; }
	public Texture getNormalsTexture() { return normalsTexture; }
	public Texture getSpecularTexture() { return specularTexture; }
	public Texture getPositionTexture() { return positionTexture; }
	public Texture getLightTexture() { return lightTexture; }
}
