package gloop.graphics.rendering.texturing;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public final class TextureUnit {
	// Unit purposes
	public static final int
		ALBEDO_MAP          = 0,
		NORMAL_MAP          = 1,
		SPECULAR_MAP        = 2,
		ENVIRONMENT_MAP     = 3,
		DEPTH_MAP           = 4,

		GBUFFER_ALBEDO      = 5,
		GBUFFER_NORMAL      = 6,
		GBUFFER_POSITION    = 7,
		GBUFFER_SPECULAR    = 8,
		GBUFFER_LIGHT       = 9;

	private static int CurrentlyBoundUnit = 0;

	private final int index;
	private Texture currentTexture;

	TextureUnit(int index) {
		this.index = index;
	}

	void bind() {
		if (CurrentlyBoundUnit == index)
			return;

		glActiveTexture(GL_TEXTURE0 + index);
		CurrentlyBoundUnit = index;
	}

	void setTexture(Texture tex) {
		if (tex == currentTexture)
			return;
		if (tex.isDisposed())
			return;

		bind();
		tex.bind();

		this.currentTexture = tex;
	}

	Texture getCurrentTexture() {
		return currentTexture;
	}
}
