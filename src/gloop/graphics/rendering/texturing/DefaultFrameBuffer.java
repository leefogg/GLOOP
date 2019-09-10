package gloop.graphics.rendering.texturing;

import gloop.graphics.rendering.Viewport;

import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

class DefaultFrameBuffer extends FrameBuffer {

	private static final String IMMUTIBLE_ERROR = "The default framebuffer is immutable.";
	private static final String UNREADABLE_ERROR = "The default framebuffer is unreadable.";
	private static final String UNDISPOSABLE_ERROR = "The default framebuffer cannot be disposed";

	public DefaultFrameBuffer() {
		this.ID = 0;
		this.width = Viewport.getWidth();
		this.height = Viewport.getHeight();
	}

	@Override
	public void createDepthAttachment() {
		throw new UnsupportedOperationException(IMMUTIBLE_ERROR);
	}

	@Override
	public FrameBufferColorTexture[] addColorAttachments(int totalattachments, PixelFormat format) {
		throw new UnsupportedOperationException(IMMUTIBLE_ERROR);
	}
	@Override
	public FrameBufferColorTexture[] addColorAttachments(PixelFormat[] formats) {
		throw new UnsupportedOperationException(IMMUTIBLE_ERROR);
	}

	@Override
	public FrameBufferColorTexture getColorTexture(int index) {
		throw new UnsupportedOperationException(UNREADABLE_ERROR);
	}
	@Override
	public Texture getDepthTexture() {
		throw new UnsupportedOperationException(UNREADABLE_ERROR);
	}

	@Override
	public FrameBufferColorTexture[] getAllColorAttachments() {
		throw new UnsupportedOperationException(UNREADABLE_ERROR);
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
		throw new UnsupportedOperationException(UNDISPOSABLE_ERROR);
	}
}
