package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class FontMaterial extends Material<FontShader> {
	private static FontShader shader;

	private Vector2f
			Scale = new Vector2f(),
			Offset = new Vector2f();
	private float
			Thickness = 0.5f,
			EdgeWidth = 0.1f;
	private Vector3f Color = new Vector3f();
	private Texture TextureAtlas;

	public FontMaterial() throws IOException {
		shader = getDefultShaderSingleton();
	}

	public static final FontShader getDefultShaderSingleton() throws IOException {
		if (shader == null)
			shader = new FontShader();

		return shader;
	}

	@Override
	public FontShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setOffset(Offset);
		shader.setScale(Scale);
		shader.setThickness(Thickness);
		shader.setEdgeWidth(EdgeWidth);
		shader.setColor(Color);

		TextureManager.bindAlbedoMap(TextureAtlas);
	}

	@Override
	protected boolean hasTransparency() {
		return true;
	}

	public void setFontTextureAtlas(Texture fonttextureatlas) { TextureAtlas = fonttextureatlas; }
	public void setScale(float width, float height) { Scale.set(width / TextureAtlas.getWidth(),  height / TextureAtlas.getHeight()); }
	public void setOffset(float x, float y) { Offset.set(x / TextureAtlas.getWidth(), y / TextureAtlas.getHeight()); }
	public void setThickness(float thickness) {
		thickness = Math.max(0, Math.min(1f, thickness));
		Thickness = 0.4f + (thickness * 0.59f);
	}
	public void setEdgeWidth(float edgeWidth) { EdgeWidth = Math.max(0, Math.min(1f-Thickness,  edgeWidth)); }
	public void setColor(Vector3f color) { Color.set(color); }
}
