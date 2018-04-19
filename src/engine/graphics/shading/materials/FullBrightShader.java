package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public class FullBrightShader extends ShaderProgram {
	private Uniform1i texture;

	public FullBrightShader() throws ShaderCompilationException, IOException {
		super(
			"res/shaders/textureshader/VertexShader.vert",
			"res/shaders/textureshader/FragmentShader.frag"
		);
	}
	FullBrightShader(String vertexfilepath, String fragmentfilepath) throws ShaderCompilationException, IOException {
		super(
				vertexfilepath,
				fragmentfilepath
			);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new Uniform1i(this, "Texture");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		texture.set(TextureUnit.AlbedoMap);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
