package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.CachedUniform1i;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public class FullBrightShader extends ShaderProgram {
	private Uniform1i Texture;

	public FullBrightShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/Texture/VertexShader.vert",
			"res/_SYSTEM/Shaders/Texture/FragmentShader.frag"
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
		Texture = new CachedUniform1i(this, "Texture");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		Texture.set(TextureUnit.AlbedoMap);
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}
}
