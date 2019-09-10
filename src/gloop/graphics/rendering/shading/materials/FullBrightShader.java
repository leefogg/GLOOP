package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;

public class FullBrightShader extends ShaderProgram {
	private Uniform1i texture;

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
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new CachedUniform1i(this, "Texture");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		texture.set(TextureUnit.ALBEDO_MAP);
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}
}
