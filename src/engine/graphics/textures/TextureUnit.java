package engine.graphics.textures;

import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

public final class TextureUnit {
	// Unit purposes
	public static final int
	AlbedoMap 	        = 0,
	NormalMap 		    = 1,
	SpecularMap         = 2,
	EnvironmentMap	    = 3,
	DepthMap            = 4,
	
	GBuffer_Albedo      = 5,
	GBuffer_Normal      = 6,
	GBuffer_Position    = 7,
	GBuffer_Specular    = 8,
	GBuffer_Light       = 9;

	private static int currentlyBoundUnit = 0;

	private final int index;
	private Texture currentTexture;

	TextureUnit(int index) {
		this.index = index;
	}

	void bind() {
		if (currentlyBoundUnit == index)
			return;

		glActiveTexture(GL_TEXTURE0 + index);
		currentlyBoundUnit = index;
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
