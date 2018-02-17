package engine.graphics.shading.materials;

import engine.graphics.rendering.Viewport;

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
	public void commit() {
		shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
		shader.setTime(Viewport.getElapsedSeconds());
		//TODO: Update mouse corordinates
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}
}
