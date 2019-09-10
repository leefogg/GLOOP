package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class FXAAPostEffect extends PostEffect<FXAAShader> {
	private static FXAAShader Shader;
	private float span = 16;

	public FXAAPostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public static FXAAShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new FXAAShader();

		return Shader;
	}

	@Override
	public FXAAShader getShader(){
		return Shader;
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}

	public void setSpan(float span) { this.span = span; }

	@Override
	public void commit() {
		Shader.setSpan(span);
	}
}
