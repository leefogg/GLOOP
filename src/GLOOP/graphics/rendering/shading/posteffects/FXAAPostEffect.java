package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class FXAAPostEffect extends PostEffect<FXAAShader> {
	private static FXAAShader shader;
	private float span = 16;

	public FXAAPostEffect() throws IOException {
		this.shader = getShaderSingleton();
	}

	public static final FXAAShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new FXAAShader();

		return shader;
	}

	@Override
	public FXAAShader getShader(){
		return shader;
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}

	public void setSpan(float span) { this.span = span; }

	@Override
	public void commit() {
		shader.setSpan(span);
	}
}