package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public final class HorizontalGaussianBlurPostEffect extends PostEffect<HorizontalGaussianBlurShader> {
	private static HorizontalGaussianBlurShader shader;

	public HorizontalGaussianBlurPostEffect() throws IOException {
		this.shader = getShaderSingleton();
	}

	public static final HorizontalGaussianBlurShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new HorizontalGaussianBlurShader();

		return shader;
	}

	@Override
	public HorizontalGaussianBlurShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setTexture(TextureUnit.AlbedoMap); // TODO: Test if this is required
		shader.setScreenWidth(Viewport.getWidth());
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}
}
