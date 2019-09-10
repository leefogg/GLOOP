package gloop.graphics.rendering.texturing;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST;

public enum TextureFilter {
	Nearest	(GL_NEAREST),
	Linear	(GL_LINEAR);
	
	private final int mode;
	TextureFilter(int mode) {
		this.mode = mode;
	}
	
	public int getGLEnum() {
		return mode;
	}
}
