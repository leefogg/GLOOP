package GLOOP.graphics.data.models;

import static org.lwjgl.opengl.GL15.*;

public enum DataVolatility {
	Static(GL_STATIC_DRAW),
	Dynamic(GL_DYNAMIC_DRAW),
	Stream(GL_STREAM_DRAW);
	
	private int GLType;
	DataVolatility(int type) {
		this.GLType = type;
	}
	public int getGLEnum() { return GLType; }
}
