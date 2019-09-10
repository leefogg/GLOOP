package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class VignettePostEffect extends PostEffect<VignetteShader> {
	private static VignetteShader Shader;

	private float
		start = 0,
		end = 0.75f;

	public VignettePostEffect() throws IOException {
		Shader = getShaderSingelton();
	}

	private VignetteShader getShaderSingelton() throws IOException {
		if (Shader == null)
			Shader = new VignetteShader();

		return Shader;
	}

	public void setStart(float start) { this.start = Math.max(0f, start); }
	public void setEnd(float end) { this.end = Math.max(start +0.01f, end); }

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}

	@Override
	public VignetteShader getShader() {	return Shader; }

	@Override
	public void commit() {
		Shader.setResolution(Viewport.getWidth(), Viewport.getHeight());

		Shader.setStart(start);
		Shader.setEnd(end);
	}
}
