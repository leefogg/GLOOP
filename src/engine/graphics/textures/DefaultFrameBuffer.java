package engine.graphics.textures;

import engine.graphics.rendering.Viewport;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

class DefaultFrameBuffer extends FrameBuffer {

	private static final String ImmutibleError = "The default framebuffer is immutable.";
	private static final String UnreadableError = "The default framebuffer is unreadable.";
	private static final String UndisposableError = "The default framebuffer cannot be disposed";

	public DefaultFrameBuffer() {
		this.ID = 0;
		this.width = Viewport.getWidth();
		this.height = Viewport.getHeight();
	}

	@Override
	public void createDepthAttachment() {
		throw new UnsupportedOperationException(ImmutibleError);
	}

	@Override
	public FrameBufferColorTexture[] addColorAttachments(int totalattachments, PixelFormat format) {
		throw new UnsupportedOperationException(ImmutibleError);
	}
	@Override
	public FrameBufferColorTexture[] addColorAttachments(PixelFormat[] formats) {
		throw new UnsupportedOperationException(ImmutibleError);
	}

	@Override
	public FrameBufferColorTexture getColorTexture(int index) {
		throw new UnsupportedOperationException(UnreadableError);
	}
	@Override
	public Texture getDepthTexture() {
		throw new UnsupportedOperationException(UnreadableError);
	}

	@Override
	public FrameBufferColorTexture[] getAllColorAttachments() {
		throw new UnsupportedOperationException(UnreadableError);
	}

	@Override
	public int getNumberOfColorAttachments() {
		return 1;
	}

	@Override
	public void bind() {
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		FrameBufferManager.setCurrentFrameBuffer(this);
	}

	@Override
	public void dispose() {
		throw new UnsupportedOperationException(UndisposableError);
	}
}
