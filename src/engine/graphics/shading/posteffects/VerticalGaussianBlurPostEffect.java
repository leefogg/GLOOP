package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public final class VerticalGaussianBlurPostEffect extends PostEffect<VerticalGaussianBlurShader> {
	private static VerticalGaussianBlurShader shader;

	public VerticalGaussianBlurPostEffect() throws IOException {
		this.shader = getShaderSingleton();
	}

	public static final VerticalGaussianBlurShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new VerticalGaussianBlurShader();

		return shader;
	}

	@Override
	public VerticalGaussianBlurShader getShader() {	return shader; }

	@Override
	public void commit() {
		shader.setTexture(TextureUnit.AlbedoMap); // TODO: Test if this is required
		shader.setScreenHeight(Viewport.getHeight());
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}
}
