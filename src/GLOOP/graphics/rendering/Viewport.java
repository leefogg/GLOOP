package GLOOP.graphics.rendering;

import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

import GLOOP.resources.ResourceManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

import GLOOP.logging.Logger;
import GLOOP.general.FrameCounter;
import org.lwjgl.util.vector.Vector2f;

public abstract class Viewport {
	private static int
			FocusedFPS = 60,
		UnfocusedFPS = 24,
		TargetFPS = FocusedFPS;
	private static boolean
		limitFrameRate;
	private static final FrameCounter framecounter = new FrameCounter();
	private static boolean unbindMouseOnUnfocus = true;
	private static long startTime = System.currentTimeMillis();

	static {
		setVSyncEnabled(true);
		limitFrameRate(true);
		unbindMouseOnBlur(true);
	}

	public static void create(String title) throws HeadlessException, LWJGLException {
		create(
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth(),
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight(),
				title
			);
	}

	public static void create(int width, int height, String title) throws LWJGLException {
		DisplayMode requesteddisplaymode = getAvailableDisplayMode(width, height, TargetFPS, true, 32);
		if (requesteddisplaymode == null)
			Display.setDisplayMode(new DisplayMode(width, height));
		else
			Display.setDisplayMode(requesteddisplaymode);

		Display.setTitle(title);
	}

	private static DisplayMode getAvailableDisplayMode(int width, int height, int fps, boolean fullscreen, int bitsperpixel) throws LWJGLException {
		for (DisplayMode mode : Display.getAvailableDisplayModes()) {
			if (mode.getFrequency() != fps)
				continue;
			if (mode.getBitsPerPixel() != bitsperpixel)
				continue;
			if (fullscreen)
				if (!mode.isFullscreenCapable())
					continue;
			if (mode.getWidth() != width)
				continue;
			if (mode.getHeight() != height)
				continue;

			return mode;
		}

		return null;
	}

	public static void update() {
		Renderer.updateTimeDelta();

		boolean displayfocused = Display.isActive();
		TargetFPS = (displayfocused) ? FocusedFPS : UnfocusedFPS;

		if (limitFrameRate)
			Display.sync(TargetFPS);

		Display.update();

		framecounter.newFrame();

		ResourceManager.disposePendingObjects();

		if (unbindMouseOnUnfocus)
			if (!displayfocused)
				Mouse.setGrabbed(false);
	}

	public static void show() throws LWJGLException {
		//TODO: Use Latest supported Opengl version for GPU
		ContextAttribs attribs = new ContextAttribs(3,3).withForwardCompatible(true);
		Display.create(new PixelFormat(0, 0, 0, 0), attribs);

		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		framecounter.start();
	}

	public static final void setFocusedFrameRate(int fps) {
		FocusedFPS = fps;
	}
	public static final void setUnfocusedFrameRate(int fps) {
		UnfocusedFPS = fps;
	}
	public static int getUnfocusedFrameRate() {
		return UnfocusedFPS;
	}
	public static int getFocusedFrameRate() {
		return FocusedFPS;
	}

	public static final int getTargetFrameRate() { return TargetFPS; }

	public static int getCurrentFrameRate() {
		return framecounter.fps;
	}


	public static void setTitle(String title) {
		Display.setTitle(title);
	}

	public static void setDimensions(int width, int height)  {
		GL11.glViewport(0, 0, width, height);
	}

	public static void setFullScreen(boolean fullscreen) throws LWJGLException {
		Display.setFullscreen(fullscreen);
	}

	public static void close() {
		Renderer.disposeAll();
		ResourceManager.disposePendingObjects();
		Display.destroy();
		Logger.dispose();
	}

	public static Vector2f getCurrentSize() { return new Vector2f(getWidth(), getHeight()); }
	public static int getWidth() {
		return Display.getWidth();
	}
	public static int getHeight() {
		return Display.getHeight();
	}
	public static String getTitle() {
		return Display.getTitle();
	}

	public static void setVSyncEnabled(boolean enabled) { Display.setVSyncEnabled(enabled); }
	public static void disableVSync() {
		setVSyncEnabled(false);
	}
	public static void enableVSync() {
		setVSyncEnabled(true);
	}

	public static float getElapsedSeconds() {
		double diff = System.currentTimeMillis() - startTime;
		return (float)diff / 1000f;
	}

	public static void limitFrameRate(boolean enableframelimit) {
		limitFrameRate = enableframelimit;
	}

	public static void unbindMouseOnBlur(boolean unbind) { unbindMouseOnUnfocus = unbind; }
}
