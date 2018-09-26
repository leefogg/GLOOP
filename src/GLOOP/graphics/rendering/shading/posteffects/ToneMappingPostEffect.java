package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class ToneMappingPostEffect extends PostEffect<ToneMappingShader> {
	private static ToneMappingShader shader;

	private float exposure = 0.75f;

	public ToneMappingPostEffect() throws IOException {
		this.shader = getShaderSingleton();
	}

	public static final ToneMappingShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new ToneMappingShader();

		return shader;
	}

	public float getExposure() { return exposure; }
	public void setExposure(float exposure) { this.exposure = exposure; }


	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}

	@Override
	public ToneMappingShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setExposure(exposure);
	}
}
