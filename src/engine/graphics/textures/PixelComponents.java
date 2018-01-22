package engine.graphics.textures;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_DEPTH_STENCIL;
import static org.lwjgl.opengl.GL30.GL_RG;

public enum PixelComponents {
	DEPTHCOMPONENT(GL_DEPTH_COMPONENT, 1),
	DEPTHSTENCIL(GL_DEPTH_STENCIL, 1),
	R(GL_RED, 1),
	RG(GL_RG, 2),
	RGB(GL_RGB, 3),
	RGBA(GL_RGBA, 4);

	private final int glenum;
	private final byte numberOfBytes;
	PixelComponents(int glenum, int bytes){
		this.glenum = glenum;
		this.numberOfBytes = (byte)bytes;
	}

	public int getGLEnum() {
		return glenum;
	}

	public int getNumberOfBytes() { return numberOfBytes; }
}
