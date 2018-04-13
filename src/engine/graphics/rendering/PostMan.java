package engine.graphics.rendering;

import engine.graphics.shading.posteffects.PostEffect;
import engine.graphics.shading.posteffects.PostProcess;
import engine.graphics.shading.posteffects.PostProcessor;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;

abstract class PostMan {
	private static FrameBuffer frontBuffer, backBuffer;
	private static Texture frontTexture, backTexture;
	private static boolean isFrontBuffer;

	public static final void init() {
		if (isActive())
			return;
		
		frontBuffer = new FrameBuffer(PixelFormat.RGB8);
		frontTexture = frontBuffer.getColorTexture(0);
		backBuffer = new FrameBuffer(PixelFormat.RGB8);
		backTexture = backBuffer.getColorTexture(0);
	}

	public static final void render(Texture initialImage, PostEffect[] effects) {
		render(initialImage);
		for (PostEffect effect : effects)
			if (effect.isEnabled())
				render(effect);
	}
	public static final void render(Texture initialImage, Iterable<PostEffect> effects) {
		render(initialImage);
		for (PostEffect effect : effects)
			if (effect.isEnabled())
				render(effect);
	}
	public static final void render(PostEffect effect) {
		render(getCurrentTexture(), effect);
	}
	private static final void render(Texture image, PostProcess effect) {
		effect.render(getCurrentBuffer(), image);
		swapBuffers();
	}
	private static final void render(Texture image) {
		getCurrentBuffer().bind();
		PostProcessor.render(image);
		swapBuffers();
	}

	private static final void swapBuffers() {
		isFrontBuffer = !isFrontBuffer;
	}

	// Gets which framebuffer to write the output to
	private static final FrameBuffer getCurrentBuffer() {
		return (isFrontBuffer) ? frontBuffer : backBuffer;
	}

	// Gets the texture to use as a source for adding the post effect onto
	private static final Texture getCurrentTexture() {
		return (isFrontBuffer) ? backTexture : frontTexture;
	}

	public static final Texture getResult() {
		return getCurrentTexture();
	}

	public static final boolean isActive() {
		return frontBuffer != null;
	}
}
