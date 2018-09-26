package GLOOP.graphics.rendering.texturing;

import GLOOP.graphics.data.DataType;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL30.*;

class FrameBufferColorBuffer extends Texture { // TODO: Test and implement. Keep hidden from end-user, cannot be bound to a uniform
	FrameBufferColorBuffer(String name, int width, int height, int attachment) {
		this(name, width, height, attachment, PixelFormat.RGB8);
	}
	FrameBufferColorBuffer(String name, int width, int height, int attachment, PixelFormat textureformat) {
		super(
				name,
				null,
				TextureTarget.Bitmap,
				PixelComponents.RGB,
				textureformat,
				TextureType.Bitmap,
				DataType.UByte,
				width,
				height
		);

		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + attachment, GL_RENDERBUFFER, ID);
	}

	@Override
	protected int generateTextureID() {
		return glGenRenderbuffers();
	}

	@Override
	public boolean bind() {
		if (isDisposed())
			return false;

		glBindRenderbuffer(GL_RENDERBUFFER, ID);
		return true;
	}

	protected void writeData(TextureTarget target, ByteBuffer pixeldata, PixelComponents externalformat, int level, int border) {
		glRenderbufferStorage(
				GL_RENDERBUFFER,
				internalFormat.getGLEnum(),
				width,
				height
		);
	}
}
