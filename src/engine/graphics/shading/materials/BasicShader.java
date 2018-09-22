package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.FragmentShader;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.shading.VertexShader;

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
