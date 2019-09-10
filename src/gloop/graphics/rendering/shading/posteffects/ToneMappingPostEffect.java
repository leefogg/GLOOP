package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class ToneMappingPostEffect extends PostEffect<ToneMappingShader> {
	private static ToneMappingShader Shader;

	private float exposure = 0.75f;

	public ToneMappingPostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public static ToneMappingShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ToneMappingShader();

		return Shader;
	}

	public float getExposure() { return exposure; }
	public void setExposure(float exposure) { this.exposure = exposure; }


	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}

	@Override
	public ToneMappingShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Shader.setExposure(exposure);
	}
}
