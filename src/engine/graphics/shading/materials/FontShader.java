package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.GLSL.Uniform2f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;

public final class FontShader extends ShaderProgram {//TODO: Replace with Shader hardcoded GLSL in Java
	private Uniform2f Scale, Offset;
	private Uniform1i TextureAtlas;

	public FontShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/FontShader/VertexShader.vert",
				"res/shaders/FontShader/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		Scale = new Uniform2f(this, "scale");
		Offset = new Uniform2f(this, "offset");
		TextureAtlas = new Uniform1i(this, "TextureAtlas");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		TextureAtlas.set(TextureUnit.AlbedoMap);

		// Cant assume character coords
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}

	public void setScale(Vector2f scale) { Scale.set(scale); }
	public void setOffset(Vector2f offset) { Offset.set(offset); }
}
