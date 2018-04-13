package engine.graphics.rendering;

import engine.graphics.models.Model;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;

public class ForwardRenderer extends Renderer {
	private boolean isDisposed;
	private FrameBuffer buffer; // TODO: Render directly into DefaultRenderBuffer

	ForwardRenderer() {
		buffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{ PixelFormat.RGB16 }, true, true);
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
