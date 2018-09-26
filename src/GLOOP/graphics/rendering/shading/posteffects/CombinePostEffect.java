package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;

import java.io.IOException;

public final class CombinePostEffect extends PostEffect<CombineShader> {
	private static CombineShader shader;

	private Texture texture1, texture2;

	public CombinePostEffect() throws IOException {
		shader = getShaderSingleton();
	}

	public CombineShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new CombineShader();
		return shader;
	}

	public void setTexture1(Texture texture) {
		texture1 = texture;
	}

	public void setTexture2(Texture texture) {
		texture2 = texture;
	}


	@Override
	public CombineShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindTextureToUnit(texture1, 5);
		TextureManager.bindTextureToUnit(texture2, 6);
		shader.setTexture1(5);
		shader.setTexture2(6);
	}

	@Override
	public void setTexture(Texture texture) {
		// Not used in the post processor
	}
}
