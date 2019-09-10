package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.BlendFunction;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.texturing.*;

import java.io.IOException;

public class BloomPostEffect extends PostEffect<ExtractBrightShader> {
	private static ExtractBrightShader Shader;
	private static FrameBuffer Vblurbuffer, Hblurbuffer;
	private static VerticalGaussianBlurPostEffect Vblureffect;
	private static HorizontalGaussianBlurPostEffect Hblureffect;
	private int passesCount = 3;

	public BloomPostEffect() throws IOException {
		Shader = getShaderSingleton();
		loadBuffers();
		loadEffects();
	}

	private void loadBuffers() {
		if (Vblurbuffer == null)
			Hblurbuffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{PixelFormat.RGB8}, false, false);
		if (Vblurbuffer == null)
			Vblurbuffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{PixelFormat.RGB8}, false, false);
	}

	private void loadEffects() throws IOException {
		if (Vblureffect == null)
			Vblureffect = new VerticalGaussianBlurPostEffect();
		if (Hblureffect == null)
			Hblureffect = new HorizontalGaussianBlurPostEffect();
	}

	public static ExtractBrightShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ExtractBrightShader();

		return Shader;
	}

	@Override
	public ExtractBrightShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {

	}

	@Override
	public void render(FrameBuffer target, Texture lastframe) {
		super.render(target, lastframe);

		Hblurbuffer.bind();
		PostProcessor.render(target.getColorTexture(0), Hblureffect);

		for (int i = 0; i< passesCount; i++) {
			Vblurbuffer.bind();
			PostProcessor.render(Hblurbuffer.getColorTexture(0), Hblureffect);
			Hblurbuffer.bind();
			PostProcessor.render(Vblurbuffer.getColorTexture(0), Vblureffect);
		}

		target.bind();
		PostProcessor.render(lastframe);
		Renderer.enableBlending(true);
		Renderer.setBlendFunctionsState(BlendFunction.One, BlendFunction.One);
		PostProcessor.render(Hblurbuffer.getColorTexture(0));
		Renderer.popBlendFunctionsState();
		Renderer.popBlendingEnabledState();
	}

	@Override
	public void setTexture(Texture texture) { TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP); }

	public void setNumberOfPasses(int passes) { passesCount = passes; }
}
