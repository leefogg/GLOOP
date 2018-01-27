package engine.graphics.shading.materials;

import java.io.IOException;

public class ShaderToyMaterial extends Material<ShaderToyShader> {
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
	protected boolean hasTransparency() {
		return false;
	}
}
