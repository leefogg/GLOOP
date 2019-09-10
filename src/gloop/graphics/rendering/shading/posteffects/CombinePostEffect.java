package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;

import java.io.IOException;

public final class CombinePostEffect extends PostEffect<CombineShader> {
	private static CombineShader Shader;

	private Texture texture1, texture2;

	public CombinePostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public CombineShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new CombineShader();
		return Shader;
	}

	public void setTexture1(Texture texture) {
		texture1 = texture;
	}

	public void setTexture2(Texture texture) {
		texture2 = texture;
	}


	@Override
	public CombineShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		TextureManager.bindTextureToUnit(texture1, 5);
		TextureManager.bindTextureToUnit(texture2, 6);
		Shader.setTexture1(5);
		Shader.setTexture2(6);
	}

	@Override
	public void setTexture(Texture texture) {
		// Not used in the post processor
	}
}
