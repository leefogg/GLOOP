package gloop.graphics.rendering.texturing;

import gloop.resources.Disposable;
import gloop.resources.Expirable;
import gloop.graphics.cameras.Camera;
import gloop.graphics.cameras.PerspectiveCamera;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Viewport;
import gloop.resources.ResourceManager;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class EnvironmentProbe implements Expirable, Disposable {
	protected static final Vector3f TEMP = new Vector3f();
	protected static final PerspectiveCamera RENDERCAM = new PerspectiveCamera(Viewport.getWidth(), Viewport.getHeight(), 90, 0.01f, 150);
	private static final int DEFAULT_RENEW_DELAY_FRAMES = -1; // Default is static. Never renew.

	protected final CubeMap environmentMap;
	protected final int faceSizePixels;
	protected final FrameBuffer frameBuffer;
	protected final int renewDelayFrames;
	protected int framesUntilRenew;

	public EnvironmentProbe(String name, int resolution, Vector3f position, Vector3f size)  {
		this(name, resolution, position, size, DEFAULT_RENEW_DELAY_FRAMES);
	}
	public EnvironmentProbe(String name, int resolution, Vector3f position, Vector3f size, int framesuntilrenew)  {
		this(new CubeMap(name + "Cubemap", resolution, PixelFormat.RGB16F, position, size), framesuntilrenew);
	}
	public EnvironmentProbe(CubeMap environmentmap) {
		this(environmentmap, DEFAULT_RENEW_DELAY_FRAMES);
	}
	public EnvironmentProbe(CubeMap environmentmap, int framesuntilrenew) {
		environmentMap = environmentmap;
		faceSizePixels = environmentmap.height; // Could be width. Cube map faces are square.
		renewDelayFrames = framesuntilrenew;
		// Leave framesUntilRenew at 0 so next frame we renew
		// TODO: frameBuffer is available once we're done updating. Could reuse framebuffer for other env probes of same resolution
		frameBuffer = new FrameBuffer(faceSizePixels, faceSizePixels, new PixelFormat[] {PixelFormat.RGB16F}, true, false);
	}

	protected void switchToFace(int face) {
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

	//TODO: Implement Updatable and renew every frame. Cant assume isExpired will be called every frame
	@Override
	public boolean isExpired() {
		if (framesUntilRenew > 0)
			framesUntilRenew--;

		return framesUntilRenew == 0;
	}

	@Override
	public void renew() {
		if (isDisposed())
			return;
		if (!isExpired())
			return;

		FrameBuffer previousframebuffer = FrameBuffer.getCurrent();

		// Use this camera
		Renderer currentrenderer = Renderer.getRenderer();
		Camera backupcam = currentrenderer.getScene().getGameCamera();
		currentrenderer.getScene().setGameCamera(RENDERCAM);

		RENDERCAM.setDimensions(faceSizePixels, faceSizePixels);
		environmentMap.getPosition(TEMP);
		RENDERCAM.setPosition(TEMP);
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

			currentrenderer.reset();
			Renderer.render();
		}

		framesUntilRenew = renewDelayFrames;

		currentrenderer.getScene().setGameCamera(backupcam);

		previousframebuffer.bind();
	}

	@Override
	public void requestDisposal() {
		ResourceManager.queueDisposal(this);
	}

	@Override
	public boolean isDisposed() {
		return environmentMap.isDisposed() || frameBuffer.isDisposed();
	}

	@Override
	public void dispose() {
		environmentMap.dispose();
		frameBuffer.dispose();
	}
}
