package gloop.graphics.rendering;

import gloop.graphics.Settings;
import gloop.graphics.rendering.shading.posteffects.PostEffect;
import gloop.graphics.rendering.shading.posteffects.PostProcess;
import gloop.graphics.rendering.shading.posteffects.PostProcessor;
import gloop.graphics.rendering.texturing.FrameBuffer;
import gloop.graphics.rendering.texturing.PixelFormat;
import gloop.graphics.rendering.texturing.Texture;

abstract class PostMan {
	private static FrameBuffer FrontBuffer, BackBuffer;
	private static Texture FrontTexture, BackTexture;
	private static boolean IsFrontBuffer;

	public static void init() {
		if (isActive())
			return;

		loadGBuffers();
	}

	private static void loadGBuffers() {
		PixelFormat pixelrangeformat = Settings.EnableHDR ? PixelFormat.RGB16F : PixelFormat.RGB8;
		FrontBuffer = new FrameBuffer(pixelrangeformat);
		FrontTexture = FrontBuffer.getColorTexture(0);
		BackBuffer = new FrameBuffer(pixelrangeformat);
		BackTexture = BackBuffer.getColorTexture(0);
	}

	public static void render(Texture initialImage, PostEffect[] effects) {
		if (effects.length == 0)
			return;

		PostProcessor.beginPostEffects();
		render(initialImage);
		for (PostEffect effect : effects)
			if (effect.isEnabled())
				render(effect);
		PostProcessor.endPostEffects();
	}
	public static void render(Texture initialImage, Iterable<PostEffect> effects) {
		render(initialImage);
		for (PostEffect effect : effects)
			if (effect.isEnabled())
				render(effect);
	}
	public static void render(PostEffect effect) {
		render(getCurrentTexture(), effect);
	}
	private static void render(Texture image, PostProcess effect) {
		effect.render(getCurrentBuffer(), image);
		swapBuffers();
	}
	private static void render(Texture image) {
		getCurrentBuffer().bind();
		PostProcessor.render(image);
		swapBuffers();
	}

	private static void swapBuffers() {
		IsFrontBuffer = !IsFrontBuffer;
	}

	// Gets which framebuffer to write the output to
	private static FrameBuffer getCurrentBuffer() {
		return (IsFrontBuffer) ? FrontBuffer : BackBuffer;
	}

	// Gets the texture to use as a source for adding the post effect onto
	private static Texture getCurrentTexture() {
		return (IsFrontBuffer) ? BackTexture : FrontTexture;
	}

	public static Texture getResult() {
		return getCurrentTexture();
	}

	public static boolean isActive() {
		return FrontBuffer != null;
	}
}
