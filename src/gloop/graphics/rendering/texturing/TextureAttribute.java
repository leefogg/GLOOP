package gloop.graphics.rendering.texturing;

import org.lwjgl.opengl.*;

public enum TextureAttribute {
	StencilMode		(GL43.GL_DEPTH_STENCIL_TEXTURE_MODE),
	BaseLevel		(GL12.GL_TEXTURE_BASE_LEVEL),
	CompareFunction	(GL14.GL_TEXTURE_COMPARE_FUNC),
	CompareMode		(GL14.GL_TEXTURE_COMPARE_MODE),
	LODBias			(GL14.GL_TEXTURE_LOD_BIAS),
	MinFilter		(GL11.GL_TEXTURE_MIN_FILTER),
	MagFilter		(GL11.GL_TEXTURE_MAG_FILTER),
	MinLOD			(GL12.GL_TEXTURE_MIN_LOD),
	MaxLOD			(GL12.GL_TEXTURE_MAX_LOD),
	MaxLevel		(GL12.GL_TEXTURE_MAX_LEVEL),
	BorderColor		(GL11.GL_TEXTURE_BORDER_COLOR),
	SwizzleA		(GL33.GL_TEXTURE_SWIZZLE_A),
	SwizzleR		(GL33.GL_TEXTURE_SWIZZLE_R),
	SwizzleG		(GL33.GL_TEXTURE_SWIZZLE_G),
	SwizzleB		(GL33.GL_TEXTURE_SWIZZLE_B),
	WrapS			(GL11.GL_TEXTURE_WRAP_S),
	WrapT			(GL11.GL_TEXTURE_WRAP_T),
	WrapR			(GL12.GL_TEXTURE_WRAP_R);
	
	private final int mode;
	TextureAttribute(int mode) {
		this.mode = mode;
	}
	
	public int getGLEnum() {
		return mode;
	}
}