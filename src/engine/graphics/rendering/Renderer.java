package engine.graphics.rendering;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColorMask;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glScissor;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;

import engine.Disposable;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.posteffects.PostEffect;
import engine.graphics.shading.posteffects.PostProcessor;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.Texture;
import org.lwjgl.opengl.Display;

import engine.graphics.cameras.Camera;
import engine.graphics.models.VertexArrayManager;
import engine.graphics.models.VertexBufferManager;
import engine.graphics.shading.ShaderManager;
import engine.graphics.textures.TextureManager;

import java.io.IOException;
import java.util.ArrayList;

public abstract class Renderer implements Disposable {
	protected Scene scene = new Scene();

	public abstract void bind(Renderer previoustechnique);
	protected abstract void renderScene();
	public abstract Texture getTexture();
	public abstract FrameBuffer getBuffer();
	public void setScene(Scene scene) { this.scene = scene; }
	public Scene getScene() { return scene; }
	@Override
	public void requestDisposal() {	dispose(); }
	public abstract void dispose();


	private static ForwardRenderer forwardRenderer;
	private static DeferredRenderer deferedRenderer;
	private static Renderer currentRenderer = null;

	private static long lastFrame = System.currentTimeMillis();
	private static float Delta;
	private static float timeScaler;
	private static boolean RenderWhenHidden = false;

	private static ArrayList<PostEffect> postEffects = new ArrayList<>();

	static {
		// Set inital state
		// Pointless (should be OpengGL defaults anyway)
		enableFaceCulling(true);
		cullBackFaces();
		enableDepthTesting(true);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL_FRAMEBUFFER_SRGB);

		setRenderer(getForwardRenderer());
	}

	public static DeferredRenderer getDeferedRenderer() throws IOException, ShaderCompilationException {
		if (deferedRenderer == null || deferedRenderer.isDisposed())
			deferedRenderer = new DeferredRenderer();
		return deferedRenderer;
	}
	public static ForwardRenderer getForwardRenderer() {
		if (forwardRenderer == null || forwardRenderer.isDisposed())
			forwardRenderer = new ForwardRenderer();
		return forwardRenderer;
	}

	public static void setRenderer(Renderer newtechnique) {
		newtechnique.bind(currentRenderer);
		currentRenderer = newtechnique;
	}
	public static Renderer getRenderer() { return currentRenderer; }


	public static void addPostEffect(PostEffect effect){ postEffects.add(effect); }

	public static void swapBuffers() {
		Texture finaltexture = currentRenderer.getTexture();

		if (PostMan.isActive()) {
			PostMan.render(finaltexture, postEffects);
			finaltexture = PostMan.getResult();
		}

		FrameBuffer.bindDefault();
		Viewport.setDimensions(finaltexture.getWidth(), finaltexture.getHeight());
		PostProcessor.render(finaltexture);

		currentRenderer.bind(null);
	}

	public static void render() {
		if (!Display.isVisible())
			return;

		currentRenderer.renderScene();
	}

	public static void enableFaceCulling(boolean enabled) {
		if (enabled)
			enableFaceCulling();
		else
			disableFaceCulling();
	}
	public static void enableFaceCulling() {
		glEnable(GL_CULL_FACE);
	}
	public static void disableFaceCulling() {
		glDisable(GL_CULL_FACE);
	}

	public static void cullFrontFaces() {
		glCullFace(GL_FRONT);
	}
	public static void cullBackFaces() { glCullFace(GL_BACK); }

	public static void enableBlending(boolean enabled) {
		if (enabled)
			enableBlending();
		else
			disableBlending();
	}
	public static void enableBlending() {glEnable(GL_BLEND); }
	public static void disableBlending() {glDisable(GL_BLEND); }

	/** Allows or blocks write access to the depth buffer */
	public static void enableDepthBuffer(boolean enabled) {
		glDepthMask(enabled);
	}
	public static void enableDepthBuffer() {
		enableDepthBuffer(true);
	}
	public static void disableDepthBuffer() {
		enableDepthBuffer(false);
	}

	public static void enableDepthTesting(boolean enabled) {
		if (enabled)
			enableDepthTesting();
		else
			disableDepthTesting();
	}
	public static void enableDepthTesting() {
		glEnable(GL_DEPTH_TEST);
	}
	public static void disableDepthTesting() {
		glDisable(GL_DEPTH_TEST);
	}

	// TODO: EnableAlpha blending
	// TODO: SetAlphaBlendingFunction

	public static void enablePostEffects() {
		PostMan.init();
	}

	public static void setDepthFunction(DepthFunction function) {
		glDepthFunc(function.getGLEnum());
	}

	public static final void enableScissorTesting() {glEnable(GL_SCISSOR_TEST);}
	public static final void disableScissorTesting() {glDisable(GL_SCISSOR_TEST);}
	public static final void setScissorArea(int x, int y, int width, int height) {
		glScissor(x, y, width, height);
	}

	/** Allows the graphics of specified color components. */
	public static void setColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		glColorMask(red, green, blue, alpha);
	}

	public static void clear(boolean colorbuffer, boolean depthbuffer, boolean stencilbuffer) {
		int mask = getBufferBits(colorbuffer, depthbuffer, stencilbuffer);

		glClear(mask);
	}

	public static final boolean willRenderWhenHidden() {
		return RenderWhenHidden;
	}
	public static final void RenderWhenHidden(boolean renderWhenHidden) {
		RenderWhenHidden = renderWhenHidden;
	}

	public static void setVoidColor(float red, float green, float blue) {
		glClearColor(
				red,
				green,
				blue,
				0
			);
	}

	public static int getBufferBits(boolean color, boolean depth, boolean stencil) {
		int mask = 0;
		// TODO: Convert to GLBufferBit enum
		if (color)
			mask |= GL_COLOR_BUFFER_BIT;
		if (depth)
			mask |= GL_DEPTH_BUFFER_BIT;
		if (stencil)
			mask |= GL_STENCIL_BUFFER_BIT;

		return mask;
	}

	public static final float getTimeDelta() { return Delta; }
	public static final float getTimeScaler() { return timeScaler; }

	public static void updateTimeDelta() {
		long now = System.nanoTime();
		Delta = (now - lastFrame) / 1000000f;
		timeScaler = Delta / (1000f / Viewport.getFocusedFrameRate());
		lastFrame = now;
	}

	public static void disposeAll() {
		VertexBufferManager.cleanup();
		VertexArrayManager.cleanup();
		TextureManager.cleanup();
		ShaderManager.cleanup();

		if (forwardRenderer != null)
			forwardRenderer.dispose();
		if (deferedRenderer != null)
			deferedRenderer.dispose();
	}

	public static void setCamera(Camera camera) { currentRenderer.scene.currentCamera = camera; }
}
