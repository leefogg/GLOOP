package gloop.graphics.rendering.texturing;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGR;
import static org.lwjgl.opengl.GL30.*;

public enum PixelComponents {
	DEPTHCOMPONENT(GL_DEPTH_COMPONENT, 1),
	DEPTHSTENCIL(GL_DEPTH_STENCIL, 2),
	STENCILINDEX(GL_STENCIL_INDEX, 1),
	R(GL_RED, 1),
	RI(GL_RED_INTEGER, 1),
	RG(GL_RG, 2),
	RGI(GL_RG_INTEGER, 2),
	RGB(GL_RGB, 3),
	RGBA(GL_RGBA, 4),
	RGBI(GL_RGB_INTEGER, 3),
	RGBAI(GL_RGBA_INTEGER, 4),
	BGR(GL_BGR, 3),
	BGRI(GL_BGR_INTEGER, 3),
	BGRAI(GL_BGRA_INTEGER, 4);

	private final int glenum;
	private final byte numberOfComponents;
	PixelComponents(int glenum, int bytes){
		this.glenum = glenum;
		this.numberOfComponents = (byte)bytes;
	}

	public int getGLEnum() {
		return glenum;
	}

	public int getNumberOfComponents() { return numberOfComponents; }
}
