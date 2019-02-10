package GLOOP.graphics.rendering;

import GLOOP.graphics.Settings;
import GLOOP.graphics.data.models.Decal;
import GLOOP.graphics.data.models.Model;
import GLOOP.graphics.data.models.Model2D;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.posteffects.PostProcessor;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glDisablei;
import static org.lwjgl.opengl.GL30.glEnablei;

public class DeferredRenderer extends Renderer {
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
	private LightingPassShader lightingshader;
	private LightingPassPostEffect lightingPosteffect;
	private boolean HDREnabled;

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
		List<String> defines = new ArrayList<>();
		if (GBuffersShader == null || GBuffersShader.isDisposed()) {
			if (Settings.EnableSpecularMapping)
				defines.add("SPECULARMAPPING");
			if (Settings.EnableNormalMapping)
				defines.add("NORMALMAPPING");
			if (Settings.EnableEnvironemntMapping)
				defines.add("ENVIRONMENTMAP");
			if (Settings.EnableReflectivity)
				defines.add("REFLECTIVITY");
			if (Settings.EnableRefractivity)
				defines.add("REFRACTIVITY");
			if (Settings.EnableChromaticAberration)
				defines.add("CHROMATICABERRATION");
			if (Settings.EnableParallaxMapping)
				defines.add("PARALLAXMAPPING");
			if (Settings.EnableFresnel)
				defines.add("FRESNEL");

			GBuffersShader = new DeferredGBuffersShader(defines.toArray(new String[0]));
		}
		if (lightingshader == null || lightingshader.isDisposed()) {
			defines.clear();
			if (Settings.EnableDither)
				defines.add("DITHER");
			if (Settings.EnableFog)
				defines.add("FOG");
			if (Settings.EnableVolumetricLights)
				defines.add("VOLUMETRICLIGHTING");

			lightingshader = new LightingPassShader(defines.toArray(new String[0]));
			lightingPosteffect = new LightingPassPostEffect(lightingshader, normalsTexture, specularTexture, positionTexture);
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

		// Calculate lights
		GBuffers.bind();
		PostProcessor.render(lightingPosteffect);

		// Blend lights with albedo texture attachment

		targetFBO.bind();
		GBuffers.blitTo(targetFBO, true, true, false); //TODO: Check stencil is making its way from the GBuffer to forward renderer
		Renderer.enableBlending(true); // Multiply not add
		Renderer.setBlendFunctionsState(BlendFunction.DestinationColor, BlendFunction.Zero);
		PostProcessor.render(lightTexture);
		Renderer.popBlendFunctionsState();
		Renderer.popBlendingEnabledState();
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

	public void setVolumetricLightsStrength(float volumetriclightsstrength) { lightingPosteffect.setVolumetricLightsStrength(volumetriclightsstrength);}

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
