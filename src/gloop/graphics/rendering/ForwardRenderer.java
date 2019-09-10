package gloop.graphics.rendering;

import gloop.graphics.Settings;
import gloop.graphics.data.models.Model;
import gloop.graphics.data.models.Model2D;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.rendering.particlesystem.ParticleSystem;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.texturing.FrameBuffer;
import gloop.graphics.rendering.texturing.PixelFormat;
import gloop.graphics.rendering.texturing.Texture;

public class ForwardRenderer extends Renderer {
	private boolean isDisposed;
	private final FrameBuffer buffer;

	ForwardRenderer() {
		PixelFormat pixelformat = Settings.EnableHDR ? PixelFormat.RGB16F : PixelFormat.RGB16;
		buffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{ pixelformat }, true, true);
	}

	@Override
	public void bind(Renderer previoustechnique) {
		if (isDisposed)
			return;

		buffer.bind();

		if (previoustechnique != null)
			if (previoustechnique instanceof ForwardRenderer)
				reset();
			else
				previoustechnique.getBuffer().blitTo(buffer, true, true, false);
	}

	@Override
	public void reset() {
		clear(true, true, true);
	}

	private void renderModels(boolean transparrent) {
		for (Model3D model : scene.getModels()) {
			if (cannotRenderModel(model))
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
		renderModels(false);
		enableBlending(true);
		setBlendFunctionsState(BlendFunction.SourceAlpha, BlendFunction.OneMinusSourceAlpha);
		renderModels(true);
		popBlendFunctionsState();
		popBlendingEnabledState();

		if (!scene.getParticleSystems().isEmpty()) {
			Renderer.enableFaceCulling(false);
			for (ParticleSystem ps : scene.getParticleSystems())
				ps.render();
			Renderer.popFaceCullingEnabledState();
		}

		//TODO: Move to first thing for efficiency
		for (Model2D overlay : scene.getOverlays())
			if (!cannotRenderModel(overlay))
				overlay.render();
	}

	public void renderShadowScene() {
		for (Model3D model : scene.getModels()) {
			Material modelMaterial = model.getMaterial();
			if (!modelMaterial.supportsShadowMaps())
				continue;

			model.render(modelMaterial.toShadowMapMaterial());
		}
	}

	private boolean cannotRenderModel(Model model) {
		return model.getMaterial().usesDeferredPipeline();
		//TODO: Add PBR material filter when added
	}

	@Override
	public Texture getTexture() {
		return buffer.getColorTexture(0);
	}

	@Override
	public FrameBuffer getBuffer() {
		return buffer;
	}

	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	public void dispose() {
		if (isDisposed)
			return;

		buffer.dispose();

		isDisposed = true;
	}
}
