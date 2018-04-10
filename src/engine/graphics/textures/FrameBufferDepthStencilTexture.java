package engine.graphics.textures;

import engine.graphics.models.DataType;
import engine.graphics.rendering.Renderer;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.*;

public final class FrameBufferDepthStencilTexture extends Texture {
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
}
