package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public final class VerticalGaussianBlurPostEffect extends PostEffect<VerticalGaussianBlurShader> {
	private static VerticalGaussianBlurShader Shader;

	public VerticalGaussianBlurPostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public static VerticalGaussianBlurShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new VerticalGaussianBlurShader();

		return Shader;
	}

	@Override
	public VerticalGaussianBlurShader getShader() {	return Shader; }

	@Override
	public void commit() {
		Shader.setTexture(TextureUnit.ALBEDO_MAP); // TODO: Test if this is required
		Shader.setScreenHeight(Viewport.getHeight());
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}
}
