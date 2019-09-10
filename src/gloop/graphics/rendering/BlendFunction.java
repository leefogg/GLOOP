package gloop.graphics.rendering;

import static org.lwjgl.opengl.GL11.*;

public enum BlendFunction {
	Zero(GL_ZERO),
	One(GL_ONE),
	SourceColor(GL_SRC_COLOR),
	OneMinusSourceColor(GL_ONE_MINUS_SRC_COLOR),
	DestinationColor(GL_DST_COLOR),
	OneMinusDestinationColor(GL_ONE_MINUS_DST_COLOR),
	SourceAlpha(GL_SRC_ALPHA),
	OneMinusSourceAlpha(GL_ONE_MINUS_SRC_ALPHA),
	DestinationAlpha(GL_DST_ALPHA),
	OneMinusDestinationAlpha(GL_ONE_MINUS_DST_ALPHA),
	ConstantColor(GL_CONSTANT_COLOR),
	OneMinusConstantColor(GL_ONE_MINUS_CONSTANT_COLOR),
	ConstantAlpha(GL_CONSTANT_ALPHA),
	OneMinusConstantAlpha(GL_ONE_MINUS_CONSTANT_ALPHA),
	SourceAlphaSaturate(GL_SRC_ALPHA_SATURATE);


	private final int GLEnum;
	BlendFunction(int glenum) {
		GLEnum = glenum;
	}
	public int getGLEnum() { return GLEnum; }
}
