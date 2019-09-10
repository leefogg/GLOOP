package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class GammaCorrectionPostEffect extends PostEffect<GammaCorrectionShader> {
	private static GammaCorrectionShader Shader;

	private float gamma = 0.75f;

	public GammaCorrectionPostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public static GammaCorrectionShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new GammaCorrectionShader();

		return Shader;
	}

	public float getGamma() { return gamma; }
	public void setGamma(float gamma) { this.gamma = gamma; }


	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}

	@Override
	public GammaCorrectionShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Shader.setGamma(gamma);
	}
}
