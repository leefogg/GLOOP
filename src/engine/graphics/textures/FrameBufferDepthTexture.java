package engine.graphics.textures;

import engine.graphics.models.DataType;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

public final class FrameBufferDepthTexture extends Texture {

	public FrameBufferDepthTexture(String name, int width, int height) {
		super(
				name,
				null,
				TextureTarget.Bitmap,
				PixelComponents.DEPTHCOMPONENT,
				PixelFormat.DEPTH8,
				TextureType.Bitmap,
				DataType.Float,
				width,
				height
			);

		GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, ID, 0);
	}
}
