package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.*;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class FontShader extends ShaderProgram {//TODO: Replace with Shader hardcoded GLSL in Java
	private Uniform2f Scale, Offset;
	private Uniform1f Thickness, EdgeWidth;
	private Uniform3f Color;
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
		Thickness = new Uniform1f(this, "Thickness");
		EdgeWidth = new Uniform1f(this, "EdgeWidth");
		Color = new Uniform3f(this, "Color");

		TextureAtlas = new CachedUniform1i(this, "TextureAtlas");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		TextureAtlas.set(TextureUnit.AlbedoMap);
		Thickness.set(0.5f);
		EdgeWidth.set(0.1f);
		Color.set(1,1,1);

		// Cant assume character coords
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}

	public void setScale(Vector2f scale) { Scale.set(scale); }
	public void setOffset(Vector2f offset) { Offset.set(offset); }
	public void setThickness(float thickness) { Thickness.set(thickness); }
	public void setEdgeWidth(float edgewidth) { EdgeWidth.set(edgewidth); }
	public void setColor(Vector3f color) { Color.set(color); }
}
