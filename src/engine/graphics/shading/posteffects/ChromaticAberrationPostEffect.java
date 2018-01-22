package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public class ChromaticAberrationPostEffect extends PostEffect<ChromaticAberrationShader> {
	private static ChromaticAberrationShader shader;
	private float offset = 3, rotation;

	public ChromaticAberrationPostEffect() throws IOException {
		shader = getShaderSingleton();
	}

	public static final ChromaticAberrationShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new ChromaticAberrationShader();

		return shader;
	}

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}

	/// Sets the thickness of the abarration in pixels
	public void setOffset(float offset) { this.offset = offset; }

	public void setRotation(float degrees) { this.rotation = (float)Math.cos(Math.toRadians(degrees)); }

	@Override
	public ChromaticAberrationShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setOffset(offset);
		shader.setRotation(rotation);
		shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
	}
}
