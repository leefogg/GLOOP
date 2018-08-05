package engine.graphics.shading.materials;

import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import org.lwjgl.util.vector.Vector2f;

import java.io.IOException;

public class FontMaterial extends Material<FontShader> {
	private static FontShader shader;

	private Vector2f
			Scale = new Vector2f(),
			Offset = new Vector2f();
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

		TextureManager.bindAlbedoMap(TextureAtlas);
	}

	@Override
	protected boolean hasTransparency() {
		return true;
	}

	public void setFontTextureAtlas(Texture fonttextureatlas) { TextureAtlas = fonttextureatlas; }
	public void setScale(float width, float height) { Scale.set(width / TextureAtlas.getWidth(),  height / TextureAtlas.getHeight()); }
	public void setOffset(float x, float y) { Offset.set(x / TextureAtlas.getWidth(), y / TextureAtlas.getHeight()); }
}
