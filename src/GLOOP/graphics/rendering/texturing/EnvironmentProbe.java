package GLOOP.graphics.rendering.texturing;

import GLOOP.resources.Expirable;
import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.cameras.PerspectiveCamera;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Viewport;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class EnvironmentProbe implements Expirable {
	private static final Vector3f Temp = new Vector3f();
	private static final PerspectiveCamera RENDERCAM = new PerspectiveCamera(Viewport.getWidth(), Viewport.getHeight(), 90, 0.01f, 1000);
	private static final int DefaultRenewDelayFrames = -1; // Default is static. Never update.

	private final CubeMap environmentMap;
	private final int faceSizePixels;
	private final FrameBuffer frameBuffer;
	private final int renewDelayFrames;
	private int framesUntilRenew;

	public EnvironmentProbe(String name, int resolution, Vector3f position, Vector3f size)  {
		this(name, resolution, position, size, DefaultRenewDelayFrames);
	}
	public EnvironmentProbe(String name, int resolution, Vector3f position, Vector3f size, int framesuntilrenew)  {
		this(new CubeMap(name + "Cubemap", resolution, PixelFormat.SRGB8, position, size), framesuntilrenew);
	}
	public EnvironmentProbe(CubeMap environmentmap) {
		this(environmentmap, DefaultRenewDelayFrames);
	}
	public EnvironmentProbe(CubeMap environmentmap, int framesuntilrenew) {
		environmentMap = environmentmap;
		faceSizePixels = environmentmap.height; // Could be width. Cube map faces are square.
		renewDelayFrames = framesuntilrenew;
		// Leave framesUntilRenew at 0 so next frame we renew
		// TODO: frameBuffer is available once we're done updating. Could reuse framebuffer for other env probes of same resolution
		frameBuffer = new FrameBuffer(faceSizePixels, faceSizePixels, new PixelFormat[] {PixelFormat.RGB8}, true, false);
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

	public void setFramesUntilRenew(int numframes) { framesUntilRenew = numframes; }

	//TODO: Implement Updatable and update every frame. Cant assume isExpired will be called every frame
	@Override
	public boolean isExpired() {
		if (framesUntilRenew > 0)
			framesUntilRenew--;

		return framesUntilRenew == 0;
	}

	@Override
	public void renew() {
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

		framesUntilRenew = renewDelayFrames;

		Renderer.getRenderer().getScene().setGameCamera(backupcam);

		previousframebuffer.bind();
	}
}
