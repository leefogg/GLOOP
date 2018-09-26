package GLOOP.graphics.rendering.texturing;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_1D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL31.GL_TEXTURE_RECTANGLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL40.GL_TEXTURE_CUBE_MAP_ARRAY;

public enum TextureType {
	List					(GL_TEXTURE_1D),
	ListArray				(GL_TEXTURE_1D_ARRAY),
	Bitmap					(GL_TEXTURE_2D),
	MultisampledBitmap		(GL_TEXTURE_2D_MULTISAMPLE),
	BitmapArray				(GL_TEXTURE_2D_ARRAY),
	MultisampledBitmapList	(GL_TEXTURE_2D_MULTISAMPLE_ARRAY),
	ThreeDimentional		(GL_TEXTURE_3D),
	Cubemap					(GL_TEXTURE_CUBE_MAP),
	CubemapArray			(GL_TEXTURE_CUBE_MAP_ARRAY),
	Rectangle				(GL_TEXTURE_RECTANGLE);


	private final int mode;
	TextureType(int mode) {
		this.mode = mode;
	}

	public int getGLEnum() {
		return mode;
	}
}
