package engine.graphics.textures;

import engine.graphics.models.DataType;
import engine.graphics.rendering.Renderer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

public final class FrameBufferDepthStencilTexture extends Texture {
	private static int
	BitMask = 0xFF,

	PassCondition = GL_NEVER,
	WriteValue = 1,
	Functionmask = 0xFF,

	FailFunction = GL_KEEP,
	zFailFunction = GL_KEEP,
	zPassFunction = GL_REPLACE;

	public FrameBufferDepthStencilTexture(String name, int width, int height) {
		super(
				name,
				null,
				TextureTarget.Bitmap, // target
				PixelComponents.DEPTHSTENCIL, // exterrnal format
				PixelFormat.DEPTH24_STENCIL8, // internal format
				TextureType.Bitmap, // type
				DataType.UInteger24_8, //data type
				width,
				height
		);

		//glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, ID);
		glFramebufferTexture(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, ID, 0);

		Renderer.checkErrors();
	}


	public static void enableStencilTesting(boolean enabled) {
		if (enabled)
			enableStencilTesting();
		else
			disableStencilTesting();
	}

	public static void enableStencilTesting() {
		glEnable(GL_STENCIL_TEST);
	}

	public static void disableStencilTesting() {
		glDisable(GL_STENCIL_TEST);
	}

	public static void clear() {
		Renderer.clear(false, false, true);
	}

	public static final int getBitMask() {
		return BitMask;
	}
	public static final void setBitMask(int mask) {
		BitMask = mask;
		glStencilMask(BitMask);
	}

	public static final int getPassCondition() {
		return PassCondition;
	}
	public static final void setPassCondition(int passcondition) {
		PassCondition = passcondition;

		glStencilFunc(PassCondition, WriteValue, Functionmask);
	}

	public static final int getWriteValue() {
		return WriteValue;
	}
	public static final void setWriteValue(int writevalue) {
		WriteValue = writevalue;

		glStencilFunc(PassCondition, WriteValue, Functionmask);
	}

	public static final int getFunctionMask() {
		return Functionmask;
	}
	public static final void setFunctionMask(int functionmask) {
		Functionmask = functionmask;

		glStencilFunc(PassCondition, WriteValue, Functionmask);
	}


	public static final int getFailFunction() {
		return FailFunction;
	}
	public static final void setFailFunction(int failfunction) {
		setFunctions(failfunction, zFailFunction, zPassFunction);
	}

	public static final int getZFailFunciton() {
		return zFailFunction;
	}
	public static final void setZFailFunction(int zfailfunction) {
		setFunctions(FailFunction, zfailfunction, zPassFunction);
	}

	public static final int getZPassFunction() {
		return zPassFunction;
	}
	public static final void setZPassFunction(int zpassfunction) {
		setFunctions(FailFunction, zFailFunction, zpassfunction);
	}

	public static final void setFunctions(int failFunction, int zfailFunction, int zpassFunction) {
		FailFunction = failFunction;
		zFailFunction = zfailFunction;
		zPassFunction = zpassFunction;

		GL11.glStencilOp(FailFunction, zFailFunction, zPassFunction);
	}

	// TODO: Implement Stencil attachment
}
