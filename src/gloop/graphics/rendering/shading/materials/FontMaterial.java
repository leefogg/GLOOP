package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class FontMaterial extends Material<FontShader> {
	private static FontShader Shader;

	private final Vector2f
			scale = new Vector2f();
	private final Vector2f offset = new Vector2f();
	private float
			thickness = 0.5f,
			edgeWidth = 0.1f;
	private final Vector3f color = new Vector3f();
	private Texture textureAtlas;

	public FontMaterial() throws IOException {
		Shader = getDefaultShaderSingleton();
	}

	public static FontShader getDefaultShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new FontShader();

		return Shader;
	}

	@Override
	public FontShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Shader.setOffset(offset);
		Shader.setScale(scale);
		Shader.setThickness(thickness);
		Shader.setEdgeWidth(edgeWidth);
		Shader.setColor(color);

		TextureManager.bindAlbedoMap(textureAtlas);
	}

	@Override
	protected boolean hasTransparency() {
		return true;
	}

	@Override
	public boolean supportsShadowMaps() { return false; }

	public void setFontTextureAtlas(Texture fonttextureatlas) { textureAtlas = fonttextureatlas; }
	public void setScale(float width, float height) { scale.set(width / textureAtlas.getWidth(),  height / textureAtlas.getHeight()); }
	public void setOffset(float x, float y) { offset.set(x / textureAtlas.getWidth(), y / textureAtlas.getHeight()); }
	public void setThickness(float thickness) {
		thickness = Math.max(0, Math.min(1f, thickness));
		this.thickness = 0.4f + (thickness * 0.59f);
	}
	public void setEdgeWidth(float edgeWidth) { this.edgeWidth = Math.max(0, Math.min(1f- thickness,  edgeWidth)); }
	public void setColor(ReadableVector3f color) { this.color.set(color); }
}
