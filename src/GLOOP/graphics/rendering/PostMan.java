package GLOOP.graphics.rendering;

import GLOOP.graphics.Settings;
import GLOOP.graphics.rendering.shading.posteffects.PostEffect;
import GLOOP.graphics.rendering.shading.posteffects.PostProcess;
import GLOOP.graphics.rendering.shading.posteffects.PostProcessor;
import GLOOP.graphics.rendering.texturing.FrameBuffer;
import GLOOP.graphics.rendering.texturing.PixelFormat;
import GLOOP.graphics.rendering.texturing.Texture;

abstract class PostMan {
	private static FrameBuffer frontBuffer, backBuffer;
	private static Texture frontTexture, backTexture;
	private static boolean isFrontBuffer;

	public static final void init() {
		if (isActive())
			return;

		loadGBuffers();
	}

	private static void loadGBuffers() {
		PixelFormat pixelrangeformat = Settings.EnableHDR ? PixelFormat.RGB16F : PixelFormat.RGB8;
		frontBuffer = new FrameBuffer(pixelrangeformat);
		frontTexture = frontBuffer.getColorTexture(0);
		backBuffer = new FrameBuffer(pixelrangeformat);
		backTexture = backBuffer.getColorTexture(0);
	}

	public static final void render(Texture initialImage, PostEffect[] effects) {
		if (effects.length == 0)
			return;

		PostProcessor.beginPostEffects();
		render(initialImage);
		for (PostEffect effect : effects)
			if (effect.isEnabled())
				render(effect);
		PostProcessor.endPostEffects();
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