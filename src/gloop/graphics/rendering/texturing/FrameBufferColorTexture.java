package gloop.graphics.rendering.texturing;

import gloop.graphics.data.DataType;
import gloop.graphics.rendering.Renderer;
import org.lwjgl.opengl.GL30;

public final class FrameBufferColorTexture extends Texture {
	FrameBufferColorTexture(String name, int width, int height, int attachment) {
		this(name, width, height, attachment, PixelFormat.RGB8);
	}
	FrameBufferColorTexture(String name, int width, int height, int attachment, PixelFormat textureformat) {
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

		setWrapMode(TextureWrapMode.EdgeClamp);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0 + attachment, TextureType.Bitmap.getGLEnum(), ID, 0);

		Renderer.checkErrors();
	}

}
