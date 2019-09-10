package gloop.graphics.data.models;

import static org.lwjgl.opengl.GL11.*;

public enum RenderMode {
	Points(GL_POINTS),
	Lines(GL_LINES),
	LineStrip(GL_LINE_STRIP),
	LineLoop(GL_LINE_LOOP),
	Triangles(GL_TRIANGLES),
	TriangleStrip(GL_TRIANGLE_STRIP),
	TriangleFan(GL_TRIANGLE_FAN);

	private final int GLType;
	RenderMode(int GLType) {
		this.GLType = GLType;
	}

	public int getGLType() {
		return GLType;
	}
}
