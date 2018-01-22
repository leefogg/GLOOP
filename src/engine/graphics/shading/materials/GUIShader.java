package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.textures.TextureUnit;

import java.io.IOException;

public final class GUIShader extends FullBrightShader {//TODO: Replace with Shader hardcoded GLSL in Java
	private Uniform1i texture;
	public GUIShader() throws ShaderCompilationException, IOException {
		super(
			"res/shaders/GUIShader/VertexShader.vert",
			"res/shaders/GUIShader/FragmentShader.frag"
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
}
