package engine.graphics.shading.posteffects;

import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public class GammaCorrectionPostEffect extends PostEffect<GammaCorrectionShader> {
	private static GammaCorrectionShader shader;

	private float gamma = 0.75f;

	public GammaCorrectionPostEffect() throws IOException {
		this.shader = getShaderSingleton();
	}

	public static final GammaCorrectionShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new GammaCorrectionShader();

		return shader;
	}

	public float getGamma() { return gamma; }
	public void setGamma(float gamma) { this.gamma = gamma; }


	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}

	@Override
	public GammaCorrectionShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setGamma(gamma);
	}
}
