package engine.graphics.models;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_24_8;

//TODO: Add all from https://www.khronos.org/registry/OpenGL-Refpages/es3.0/html/glTexImage2D.xhtml type
public enum DataType {
	// Integer data types
	Byte	(GL_BYTE, 1),
	UByte	(GL_UNSIGNED_BYTE, 1),
	Short	(GL_SHORT, 2),
	UShort	(GL_UNSIGNED_SHORT, 2),
	Integer	(GL_INT, 4),
	UInteger(GL_UNSIGNED_INT, 4),
	UInteger24_8(GL_UNSIGNED_INT_24_8, 32),
	//TODO: Does GPU support long data type?
	// Floating-point data types
	Float	(GL_FLOAT, 4),
	Double	(GL_DOUBLE, 8);

	private final int GLType, Size;
	DataType(int type, int size) {
		this.GLType = type;
		this.Size = size;
	}

	public int getGLEnum() {
		return GLType;
	}

	public int getSize() {
		return Size;
	}

	public static DataType getSmallest(long minvalue, long maxvalue) {
		//TODO: Double check these ranges in the OpenGL spec
		if (minvalue >= 0) { // Unsigned types
			if (maxvalue <= java.lang.Byte.MAX_VALUE)
				return UByte;
			if (maxvalue <= java.lang.Short.MAX_VALUE)
				return UShort;

			return UInteger;
		}

		// Signed types
		if (maxvalue <= java.lang.Byte.MAX_VALUE/2-1)
			return Byte;
		if (maxvalue <= java.lang.Short.MAX_VALUE/2-1)
			return Short;

		return Integer;
	}
	public static DataType getSmallest(float maxvalue) {
		if (maxvalue < java.lang.Float.MAX_VALUE)
			return Float;

		return Double;
	}
}
