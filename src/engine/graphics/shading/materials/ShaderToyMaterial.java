package engine.graphics.shading.materials;

import engine.graphics.shading.posteffects.PostEffect;
import engine.graphics.textures.Texture;

import java.io.IOException;

public class ShaderToyMaterial extends PostEffect<ShaderToyShader> {
	private ShaderToyShader shader;

	public ShaderToyMaterial(String fragmentshaderpath) throws IOException {
		shader = new ShaderToyShader(
				"res/shaders/Post Effects/shadertoy/vertexShader.vert",
				fragmentshaderpath
		);
	}

	@Override
	public ShaderToyShader getShader() {
		return shader;
	}

	@Override
	public void commit() { shader.setOptionalUniformValues(); }

	@Override
	public void setTexture(Texture texture) {

	}
}
