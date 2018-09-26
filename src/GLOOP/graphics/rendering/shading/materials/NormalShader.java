package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;

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
