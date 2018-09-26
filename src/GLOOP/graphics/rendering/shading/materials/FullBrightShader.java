package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.texturing.TextureUnit;

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
