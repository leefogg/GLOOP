package engine.graphics.textures;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.PerspectiveCamera;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Viewport;
import org.lwjgl.opengl.GL13;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class EnvironmentProbe {
	private static final PerspectiveCamera RENDERCAM = new PerspectiveCamera(Viewport.getWidth(), Viewport.getHeight(), 90, 0.01f, 1000);

	private final CubeMap EnvironmentMap;
	private final int FaceSizePixels;
	private final FrameBuffer FrameBuffer;
	private final Vector3f Position;

	public EnvironmentProbe(String name, int size, Vector3f position) throws IOException {
		this(new CubeMap(name + "Cubemap", size, PixelFormat.SRGB8), position);
	}
	public EnvironmentProbe(CubeMap environmentmap, Vector3f position) {
		EnvironmentMap = environmentmap;
		FaceSizePixels = environmentmap.height;
		FrameBuffer = new FrameBuffer(FaceSizePixels, FaceSizePixels, new PixelFormat[] {PixelFormat.RGB8}, true, false);
		Position = position;
	}

	public void update() {
		FrameBuffer previousframebuffer = FrameBuffer.getCurrent();

		// Use this camera
		Camera backupcam = Renderer.getCurrentCamera();
		Renderer.getRenderer().getScene().setGameCamera(RENDERCAM);

		RENDERCAM.setDimensions(FaceSizePixels, FaceSizePixels);
		RENDERCAM.setPosition(Position);
		EnvironmentMap.bind();
		FrameBuffer.bind();

		for (int i=0; i<6; i++) {
			glFramebufferTexture2D(
					GL_FRAMEBUFFER,
					GL_COLOR_ATTACHMENT0,
					GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					EnvironmentMap.ID,
					0
			);

			switchToFace(i);

			Renderer.clear(true, true, false);
			Renderer.render();
		}

		Renderer.getRenderer().getScene().setGameCamera(backupcam);

		previousframebuffer.bind();
	}

	private void switchToFace(int face) {
		switch (face) {
			case 0: RENDERCAM.setRotation(0,270,180);  break; // Left
			case 1:	RENDERCAM.setRotation(0,90,180);   break; // Right
			case 2:	RENDERCAM.setRotation(-90,0,0);    break; // Top
			case 3:	RENDERCAM.setRotation(90,0,0);     break; // Bottom
			case 4:	RENDERCAM.setRotation(0,180,180);  break; // Forward
			case 5:	RENDERCAM.setRotation(0,0,180);    break; // Backward
		}
	}

	public void setPosition(Vector3f position) { Position.set(position); }

	public CubeMap getEnvironmentMap() { return EnvironmentMap; }
}
