package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.models.Model;
import engine.graphics.particlesystem.ParticleSystem;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;

public class ForwardRenderer extends Renderer {
	private boolean isDisposed;
	private FrameBuffer buffer;

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
				Renderer.clear(true, true, true);
			else
				previoustechnique.getBuffer().blitTo(buffer, true, true, false);
	}

	@Override
	protected void renderScene() {
		for (Model model : scene.getModels()) {
			if (model.getMaterial().usesDeferredPipeline())
				continue;
			//TODO: Add PBR material filter when added

			model.render();
		}

		Renderer.enableFaceCulling(false);
		for (ParticleSystem ps : scene.getParticleSystems()) {
			ps.render();
		}
		Renderer.popFaceCullingState();
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
