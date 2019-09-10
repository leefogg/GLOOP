package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.FragmentShader;
import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.shading.VertexShader;
import gloop.graphics.rendering.texturing.TextureUnit;

import java.io.IOException;
import java.util.Map;

public class PostEffectShader extends ShaderProgram {
	private Uniform1i texture1;

	public PostEffectShader(String vertexshader, String fragmentshader) throws ShaderCompilationException, IOException {
		super(vertexshader, fragmentshader);
	}
	public PostEffectShader(String vertexshader, String fragmentshader, Iterable<Map.Entry<String, String>> defines) throws ShaderCompilationException, IOException {
		super(vertexshader, fragmentshader, defines);
	}
	public PostEffectShader(VertexShader vertexshader, FragmentShader fragmentshader) {
		super(vertexshader, fragmentshader);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture1 = new CachedUniform1i(this, "Texture");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setTextureUnit(TextureUnit.ALBEDO_MAP);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}

	public void setTextureUnit(int unit) { texture1.set(unit); }
}
