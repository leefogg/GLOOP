package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class ChromaticAberrationPostEffect extends PostEffect<ChromaticAberrationShader> {
	private static ChromaticAberrationShader Shader;

	private float offset = 3, rotation;

	public ChromaticAberrationPostEffect() throws IOException {
		Shader = getShaderSingleton();
	}

	public static ChromaticAberrationShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ChromaticAberrationShader();

		return Shader;
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}

	/// Sets the thickness of the abarration in pixels
	public void setOffset(float offset) { this.offset = offset; }

	public void setRotation(float degrees) { this.rotation = (float)Math.cos(Math.toRadians(degrees)); }

	@Override
	public ChromaticAberrationShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Shader.setOffset(offset);
		Shader.setRotation(rotation);
		Shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
	}
}
