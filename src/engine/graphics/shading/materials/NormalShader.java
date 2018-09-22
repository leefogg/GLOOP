package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;

import java.io.IOException;

public class NormalShader extends ShaderProgram {
	public NormalShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Normal/vertexShader.vert",
				"res/_SYSTEM/Shaders/Normal/fragmentShader.frag"
			);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("VertexNormal", VertexArray.VertexNormalsIndex);
	}

	@Override
	protected void getCustomUniformLocations() {}

	@Override
	protected void setDefaultCustomUniformValues() {}

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
