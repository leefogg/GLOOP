package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;

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
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("VertexNormal", VertexArray.VERTEX_NORMALS_INDEX);
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
