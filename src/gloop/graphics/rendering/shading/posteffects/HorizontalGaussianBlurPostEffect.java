package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public final class HorizontalGaussianBlurPostEffect extends PostEffect<HorizontalGaussianBlurShader> {
	private static HorizontalGaussianBlurShader Shader;

	public HorizontalGaussianBlurPostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public static HorizontalGaussianBlurShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new HorizontalGaussianBlurShader();

		return Shader;
	}

	@Override
	public HorizontalGaussianBlurShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Shader.setTexture(TextureUnit.ALBEDO_MAP); // TODO: Test if this is required
		Shader.setScreenWidth(Viewport.getWidth());
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}
}
