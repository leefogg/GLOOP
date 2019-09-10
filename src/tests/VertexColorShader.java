package tests;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;

import java.io.IOException;

public class VertexColorShader extends ShaderProgram {
	public VertexColorShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Tests/VertexColors/VertexShader.vert",
				"res/shaders/Tests/VertexColors/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
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
