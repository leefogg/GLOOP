package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public class VignettePostEffect extends PostEffect<VignetteShader> {
	private static VignetteShader shader;
	private float Strength = 5;

	public VignettePostEffect() throws IOException {
		shader = getShaderSingelton();
	}

	private VignetteShader getShaderSingelton() throws IOException {
		if (shader == null)
			shader = new VignetteShader();

		return shader;
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}


	@Override
	public VignetteShader getShader() {	return shader; }

	@Override
	public void commit() {
		shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
		shader.setTime(Viewport.getElapsedSeconds());

		shader.setStrength(Strength);
	}

	public void setStrength(float strength) { Strength = strength; }
}
