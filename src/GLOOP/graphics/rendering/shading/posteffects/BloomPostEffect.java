package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.BlendFunction;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.texturing.*;

import java.io.IOException;

public class BloomPostEffect extends PostEffect<ExtractBrightShader> {
	private static ExtractBrightShader shader;
	private static FrameBuffer vblurbuffer, hblurbuffer;
	private static VerticalGaussianBlurPostEffect vblureffect;
	private static HorizontalGaussianBlurPostEffect hblureffect;
	private int PassesCount = 3;

	public BloomPostEffect() throws IOException {
		shader = getShaderSingleton();
		loadBuffers();
		loadEffects();
	}

	private void loadBuffers() {
		if (vblurbuffer == null)
			hblurbuffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{PixelFormat.RGB8}, false, false);
		if (vblurbuffer == null)
			vblurbuffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{PixelFormat.RGB8}, false, false);
	}

	private void loadEffects() throws IOException {
		if (vblureffect == null)
			vblureffect = new VerticalGaussianBlurPostEffect();
		if (hblureffect == null)
			hblureffect = new HorizontalGaussianBlurPostEffect();
	}

	public static ExtractBrightShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new ExtractBrightShader();

		return shader;
	}

	@Override
	public ExtractBrightShader getShader() {
		return shader;
	}

	@Override
	public void commit() {

	}

	@Override
	public void render(FrameBuffer target, Texture lastframe) {
		super.render(target, lastframe);

		hblurbuffer.bind();
		PostProcessor.render(target.getColorTexture(0), hblureffect);

		for (int i=0; i<PassesCount; i++) {
			vblurbuffer.bind();
			PostProcessor.render(hblurbuffer.getColorTexture(0), hblureffect);
			hblurbuffer.bind();
			PostProcessor.render(vblurbuffer.getColorTexture(0), vblureffect);
		}

		target.bind();
		PostProcessor.render(lastframe);
		Renderer.enableBlending(true);
		Renderer.setBlendFunctionsState(BlendFunction.One, BlendFunction.One);
		PostProcessor.render(hblurbuffer.getColorTexture(0));
		Renderer.popBlendFunctionsState();
		Renderer.popBlendingEnabledState();
	}

	@Override
	public void setTexture(Texture texture) { TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap); }

	public void setNumberOfPasses(int passes) { PassesCount = passes; }
}
