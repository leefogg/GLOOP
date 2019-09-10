package gloop.graphics.rendering;

import static org.lwjgl.opengl.GL11.*;

public enum DepthFunction {
	Never			(GL_NEVER),
	Less			(GL_LESS),
	Equal			(GL_EQUAL),
	Greater			(GL_GREATER),
	LessOrEqual		(GL_LEQUAL),
	NotEqual		(GL_NOTEQUAL),
	GreaterOrEqual	(GL_GEQUAL),
	Always			(GL_ALWAYS);


	private final int mode;
	DepthFunction(int mode) {
		this.mode = mode;
	}

	public int getGLEnum() {
		return mode;
	}
}
