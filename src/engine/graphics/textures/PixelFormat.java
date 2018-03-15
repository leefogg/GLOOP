package engine.graphics.textures;

import engine.graphics.data.DataConversion;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;

//TODO: Import all from GL30

// From ftp://ftp.sgi.com/opengl/contrib/blythe/advanced99/notes/node51.html
public enum PixelFormat {
	RED         (GL_RED,                8,  0,  0,  0),
	GREEN       (GL_GREEN,              0,  8,  0,  0),
	BLUE        (GL_BLUE,               0,  0,  8,  0),
	RGB4        (GL_RGB4,               4,  4,  4,  0),
	RGB5        (GL_RGB5,               5,  5,  5,  0),
	RGB5A1      (GL_RGB5_A1,            5,  5,  5,  1),
	RGB8        (GL_RGB8,               8,  8,  8,  0),
	RGB8I       (GL_RGB8I,              8,  8,  8,  0),
	SRGB8       (GL_SRGB8,              8,  8,  8,  0),
	RGB10       (GL_RGB10,              10, 10, 10, 0),
	RGB10A2     (GL_RGB10_A2,           10, 10, 10, 2),
	RGB12       (GL_RGB12,              12, 12, 12, 0),
	RGB16       (GL_RGB16,              16, 16, 16, 0),
	RGBA2       (GL_RGBA2,              2,  2,  2,  2),
	RGBA4       (GL_RGBA4,              4,  4,  4,  4),
	RGBA8       (GL_RGBA8,              8,  8,  8,  8),
	SRGBA8      (GL_SRGB8_ALPHA8,       8,  8,  8,  8),
	RGBA12      (GL_RGBA12,             12, 12, 12, 12),
	RGBA16      (GL_RGBA16,             16, 16, 16, 16),
	RGB16F      (GL_RGB16F,             16, 16, 16, 16),
	RGB16I      (GL_RGB16I,             16, 16, 16, 16),
	RGBA16F     (GL_RGB16F,             16, 16, 16, 16),
	RGB32F      (GL_RGB32F,             32, 32, 32, 0),
	R3G3B2      (GL_R3_G3_B2,           3,  3,  2,  0),
	DEPTH8      (GL_DEPTH_COMPONENT,    0,  0,  0,  8),
	DEPTH16     (GL_DEPTH_COMPONENT16,  0,  0,  0,  16),
	DEPTH24     (GL_DEPTH_COMPONENT24,  0,  0,  0,  24),
	DEPTH32     (GL_DEPTH_COMPONENT32,  0,  0,  0,  32),
	DEPTH32F    (GL_DEPTH_COMPONENT32F, 0,  0,  0,  32);

	//TODO: Add GLDataType
	//TODO: Add isFullFloatingPoint
	private final int glenum, redBits, greenBits, blueBits, alphaBits;
	PixelFormat(int glenum, int redbits, int greenbits, int bluebits, int alphabits) {
		this.glenum = glenum;
		this.redBits = redbits;
		this.greenBits = greenbits;
		this.blueBits = bluebits;
		this.alphaBits = alphabits;
	}

	public final int getGLEnum() {
		return glenum;
	}

	public final int getRedBits() {
		return redBits;
	}

	public final int getGreenBits() {
		return greenBits;
	}

	public final int getBlueBits() {
		return blueBits;
	}

	public final int getAlphaBits() {
		return alphaBits;
	}

	public final int getTotalBits() {
		return getGreenBits() + getGreenBits() + getBlueBits() + getAlphaBits();
	}

	public final int getSize() {
		return DataConversion.nextPowerOf2(getTotalBits());
	}
}
