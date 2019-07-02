package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.FragmentShader;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.shading.VertexShader;
import GLOOP.graphics.rendering.texturing.TextureUnit;

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
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture1 = new CachedUniform1i(this, "Texture");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setTextureUnit(TextureUnit.AlbedoMap);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}

	public void setTextureUnit(int unit) { texture1.set(unit); }
}
