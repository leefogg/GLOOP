package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.FragmentShader;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.shading.VertexShader;

public class BasicShader extends ShaderProgram {
	public BasicShader(VertexShader vertexshader, FragmentShader fragmentshader) {
		super(vertexshader, fragmentshader);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
		bindAttribute("VertexNormal", VertexArray.VertexNormalsIndex);
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
