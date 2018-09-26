package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class VignettePostEffect extends PostEffect<VignetteShader> {
	private static VignetteShader shader;

	private float
			Start = 0,
			End = 0.75f;

	public VignettePostEffect() throws IOException {
		shader = getShaderSingelton();
	}

	private VignetteShader getShaderSingelton() throws IOException {
		if (shader == null)
			shader = new VignetteShader();

		return shader;
	}

	public void setStart(float start) { Start = Math.max(0f, start); }
	public void setEnd(float end) { End = Math.max(Start+0.01f, end); }

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}

	@Override
	public VignetteShader getShader() {	return shader; }

	@Override
	public void commit() {
		shader.setResolution(Viewport.getWidth(), Viewport.getHeight());

		shader.setStart(Start);
		shader.setEnd(End);
	}
}
