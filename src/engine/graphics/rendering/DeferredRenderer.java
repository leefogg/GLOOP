package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.models.Decal;
import engine.graphics.models.Model2D;
import engine.graphics.models.Model3D;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.materials.FullBrightMaterial;
import engine.graphics.shading.posteffects.PostProcessor;
import engine.graphics.textures.*;
import engine.graphics.rendering.UI.GUIRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

	private static Model2D[] UIs;

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
			if (!model.isVisible())
				continue;
			if (model.getMaterial().isTransparent() != transparrent)
				continue;

			model.render();
		}
	}

	@Override
	protected void renderScene() {
		int[] boundrenderattachments = GBuffers.getBoundColorAttachments();

		clear(true, true, false);

		renderModels(false);
		glEnablei(GL_BLEND, 0);
		setBlendFunctionsState(BlendFunction.SourceAlpha, BlendFunction.OneMinusSourceAlpha);
		renderModels(true);
		popBlendFunctionsState();
		glDisablei(GL_BLEND, 0);


		for (Decal decal : scene.getDecals()) {
			decal.render();

			GBuffers.bindRenderAttachments(boundrenderattachments);
		}

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

		// Calculate lighting
		GBuffers.bind();
		PostProcessor.render(lightingPosteffect);

		// Blend lighting with albedo texture attachment

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

	public void renderAttachments() {
		renderAttachments(8);
	}
	public void renderAttachments(float scaledivisor) {
		if (isDisposed)
			return;

		// Always render to the screen so aren't affected by post effects
		FrameBuffer.bindDefault();
		Viewport.setDimensions(Viewport.getWidth(), Viewport.getHeight());

		ensureUIsExist();

		int	viewportheight = Viewport.getHeight(),
			viewportwidth = Viewport.getWidth(),
			width = (int)Math.floor(viewportwidth / scaledivisor),
			height = (int)Math.floor(viewportheight / scaledivisor),
			x = 0,
			y = 0;

		int numattachments = GBuffers.getNumberOfColorAttachments() + targetFBO.getNumberOfColorAttachments();
		Texture[] allattachments = new Texture[numattachments];

		int i=0;
		Texture[] attachments = GBuffers.getAllColorAttachments();
		for (int attachmentindex=0; attachmentindex<attachments.length; i++, attachmentindex++)
			allattachments[i] = attachments[attachmentindex];
		attachments = targetFBO.getAllColorAttachments();
		for (int attachmentindex=0; attachmentindex<attachments.length; i++, attachmentindex++)
			allattachments[i] = attachments[attachmentindex];

		for (i=0; i<allattachments.length; i++) {
			Model2D ui = UIs[i];

			ui.setScale(width, height);

			ui.setPosition(x, y);

			x += width;
			if (x+width > viewportwidth) {
				y += height;
				x = 0;
			}

			((FullBrightMaterial)ui.getMaterial()).setAlbedoTexture(allattachments[i]);
		}

		GUIRenderer.render(UIs);
	}

	private void ensureUIsExist() {
		int numattachments = GBuffers.getNumberOfColorAttachments() + targetFBO.getNumberOfColorAttachments() ;

		if (UIs == null) {
			UIs = new Model2D[numattachments];

			for (int i=0; i<numattachments; i++)
				UIs[i] = new Model2D(0,0,0,0);
		} else if (UIs.length < numattachments) {
			int oldsize = UIs.length;
			UIs = Arrays.copyOf(UIs, numattachments);

			for (int i=oldsize; i<UIs.length; i++)
				UIs[i] = new Model2D(0,0,0,0);
		}
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

	//TODO: Incomplete crap
	public void read() {
		GBuffers.bind();
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, TextureType.Bitmap.getGLEnum(), positionTexture.getID(), 0);
		Vector4f color = FrameBuffer.read(Mouse.getX(), Mouse.getY());
		System.out.println(String.format("%10.40f",color.x) + " " + String.format("%10.40f",color.y) + " " + String.format("%10.40f",color.z));
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT1, TextureType.Bitmap.getGLEnum(), positionTexture.getID(), 0);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, TextureType.Bitmap.getGLEnum(), albedoTexture.getID(), 0);
	}
}
