package engine.graphics.rendering;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_FRONT;

public enum CullFaceState {
	Front(GL_FRONT),
	Back(GL_BACK);

	private int GLEnum;
	CullFaceState(int glenum) {
		this.GLEnum = glenum;
	}
	public int getGLEnum() { return GLEnum; }
}
