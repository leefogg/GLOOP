package gloop.graphics.rendering.texturing;

import java.util.HashSet;

public class FrameBufferManager {
	private static final HashSet<FrameBuffer> FRAME_BUFFERS = new HashSet<>();
	private static FrameBuffer CurrentFrameBuffer = FrameBuffer.DEFAULT_FRAME_BUFFER;

	static void register(FrameBuffer frameBuffer) { FRAME_BUFFERS.add(frameBuffer); }
	static void unregister(FrameBuffer frameBuffer) { FRAME_BUFFERS.remove(frameBuffer); }

	public static FrameBuffer getCurrentFrameBuffer() { return CurrentFrameBuffer; }
	static void setCurrentFrameBuffer(FrameBuffer framebuffer) { CurrentFrameBuffer = framebuffer; }

	public static void cleanup() {
		System.out.println("Deleting " + FRAME_BUFFERS.size() + " FrameBuffers...");
		for (FrameBuffer framebuffer : FRAME_BUFFERS)
			framebuffer.dispose();
		FRAME_BUFFERS.clear();
	}
}
