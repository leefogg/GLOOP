package engine.graphics.models;

import org.lwjgl.opengl.GL15;

//TODO: Add all types from https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glBufferData.xhtml
public enum GLArrayType {
	Array(GL15.GL_ARRAY_BUFFER),
	Element(GL15.GL_ELEMENT_ARRAY_BUFFER);
	
	private int GLType;
	GLArrayType(int type) {
		GLType = type;
	}
	
	public int getGLEnum() {
		return GLType;
	}
}
