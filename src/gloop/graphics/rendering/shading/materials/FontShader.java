package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.*;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class FontShader extends ShaderProgram {//TODO: Replace with Shader hardcoded glsl in Java
	private Uniform2f scale, offset;
	private Uniform1f thickness, edgeWidth;
	private Uniform3f color;
	private Uniform1i textureAtlas;

	public FontShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Font/VertexShader.vert",
				"res/_SYSTEM/Shaders/Font/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		scale = new Uniform2f(this, "scale");
		offset = new Uniform2f(this, "offset");
		thickness = new Uniform1f(this, "Thickness");
		edgeWidth = new Uniform1f(this, "EdgeWidth");
		color = new Uniform3f(this, "Color");

		textureAtlas = new CachedUniform1i(this, "TextureAtlas");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		textureAtlas.set(TextureUnit.ALBEDO_MAP);
		thickness.set(0.5f);
		edgeWidth.set(0.1f);
		color.set(1,1,1);

		// Cant assume character coords
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}

	public void setScale(Vector2f scale) { this.scale.set(scale); }
	public void setOffset(Vector2f offset) { this.offset.set(offset); }
	public void setThickness(float thickness) { this.thickness.set(thickness); }
	public void setEdgeWidth(float edgewidth) { edgeWidth.set(edgewidth); }
	public void setColor(Vector3f color) { this.color.set(color); }
}
