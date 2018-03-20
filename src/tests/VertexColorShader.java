package tests;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;

import java.io.IOException;

public class VertexColorShader extends ShaderProgram {
	public VertexColorShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/vertexcolors/VertexShader.vert",
				"res/shaders/vertexcolors/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("Color", 2);
	}

	@Override
	protected void getCustomUniformLocations() {

	}

	@Override
	protected void setDefaultCustomUniformValues() {

	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
