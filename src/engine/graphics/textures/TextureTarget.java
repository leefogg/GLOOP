package engine.graphics.textures;

import static org.lwjgl.opengl.GL11.GL_PROXY_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL30.GL_PROXY_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL31.GL_PROXY_TEXTURE_RECTANGLE;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;

public enum TextureTarget {
	Bitmap 			(GL_TEXTURE_2D),
	BitmapProxy		(GL_PROXY_TEXTURE_2D),
	List			(GL_TEXTURE_1D_ARRAY),
	ListProxy		(GL_PROXY_TEXTURE_1D_ARRAY),
	Rectangle		(GL_TEXTURE_RECTANGLE),
	RectangleProxy	(GL_PROXY_TEXTURE_RECTANGLE),
	CubeMapRight	(GL_TEXTURE_CUBE_MAP_POSITIVE_X),
	CubeMapLeft		(GL_TEXTURE_CUBE_MAP_NEGATIVE_X),
	CubeMapTop		(GL_TEXTURE_CUBE_MAP_POSITIVE_Y),
	CubeMapBottom	(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y),
	CubeMapBack		(GL_TEXTURE_CUBE_MAP_POSITIVE_Z),
	CubeMapFront	(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z),
	CubeMapProxy	(GL_PROXY_TEXTURE_CUBE_MAP);


	private final int mode;
	TextureTarget(int mode) {
		this.mode = mode;
	}

	public int getGLEnum() {
		return mode;
	}
}
