package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public final class VerticalGaussianBlurPostEffect extends PostEffect<VerticalGaussianBlurShader> {
	private final VerticalGaussianBlurShader shader;

	public VerticalGaussianBlurPostEffect() throws IOException {
		this.shader = new VerticalGaussianBlurShader();
	}

	@Override
	public VerticalGaussianBlurShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setTexture(TextureUnit.AlbedoMap); // TODO: Test if this is required
		shader.setScreenHeight(Viewport.getHeight()/4);
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}
}
