package engine.graphics.shading.posteffects;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.FragmentShader;
import engine.graphics.shading.GLSL.CachedUniform1i;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.shading.VertexShader;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public class PostEffectShader extends ShaderProgram {
	private Uniform1i texture1;

	public PostEffectShader(String vertexshader, String fragmentshader) throws ShaderCompilationException, IOException {
		super(vertexshader, fragmentshader);
	}
	public PostEffectShader(String vertexshader, String fragmentshader, String[] defines) throws ShaderCompilationException, IOException {
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
