package GLOOP.graphics.rendering.texturing;

import java.util.HashSet;

public class FrameBufferManager {
	private static final HashSet<FrameBuffer> framebuffers = new HashSet<>();
	private static FrameBuffer currentFrameBuffer = FrameBuffer.DEFAULT_FRAME_BUFFER;

	static void register(FrameBuffer frameBuffer) {	framebuffers.add(frameBuffer); }
	static void unregister(FrameBuffer frameBuffer) {framebuffers.remove(frameBuffer); }

	public static FrameBuffer getCurrentFrameBuffer() { return currentFrameBuffer; }
	static void setCurrentFrameBuffer(FrameBuffer framebuffer) { currentFrameBuffer = framebuffer; }

	public static void cleanup() {
		System.out.println("Deleting " + framebuffers.size() + " FrameBuffers...");
		for (FrameBuffer framebuffer : framebuffers)
			framebuffer.dispose();
		framebuffers.clear();
	}
}