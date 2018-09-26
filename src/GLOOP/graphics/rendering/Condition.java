package GLOOP.graphics.rendering;

import static org.lwjgl.opengl.GL11.*;

public enum Condition {
	Never           (GL_NEVER),
	LessThan        (GL_LESS),
	GreaterThan     (GL_GREATER),
	Equals          (GL_EQUAL),
	Always          (GL_ALWAYS),
	LessOrEquals    (GL_LEQUAL),
	GreaterOrEquals (GL_GEQUAL),
	NotEquals       (GL_NOTEQUAL);

	private final int GLEnum;
	Condition(int glenum) {
		this.GLEnum = glenum;
	}

	public int getGLEnum() { return GLEnum; }
}
