package engine.graphics.textures;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.PerspectiveCamera;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Viewport;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class EnvironmentProbe {
	private static final Vector3f Temp = new Vector3f();
	private static final PerspectiveCamera RENDERCAM = new PerspectiveCamera(Viewport.getWidth(), Viewport.getHeight(), 90, 0.01f, 1000);

	private final CubeMap environmentMap;
	private final int faceSizePixels;
	private final FrameBuffer frameBuffer;

	public EnvironmentProbe(String name, int resolution, Vector3f position, Vector3f size)  {
		this(new CubeMap(name + "Cubemap", resolution, PixelFormat.SRGB8, position, size));
	}
	public EnvironmentProbe(CubeMap environmentmap) {
		environmentMap = environmentmap;
		faceSizePixels = environmentmap.height; // Could be width. Cube map faces are square.
		// TODO: frameBuffer is available once we're done updating. Could reuse framebuffer for other env probes of same resolution
		frameBuffer = new FrameBuffer(faceSizePixels, faceSizePixels, new PixelFormat[] {PixelFormat.RGB8}, true, false);
	}

	public void update() {
		FrameBuffer previousframebuffer = frameBuffer.getCurrent();

		// Use this camera
		Camera backupcam = Renderer.getCurrentCamera();
		Renderer.getRenderer().getScene().setGameCamera(RENDERCAM);

		RENDERCAM.setDimensions(faceSizePixels, faceSizePixels);
		environmentMap.getPosition(Temp);
		RENDERCAM.setPosition(Temp);
		environmentMap.bind();
		frameBuffer.bind();

		for (int i=0; i<6; i++) {
			glFramebufferTexture2D(
					GL_FRAMEBUFFER,
					GL_COLOR_ATTACHMENT0,
					GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
					environmentMap.ID,
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

	public CubeMap getEnvironmentMap() { return environmentMap; }
}
