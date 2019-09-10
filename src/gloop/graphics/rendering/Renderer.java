package gloop.graphics.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;

import gloop.graphics.Settings;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.resources.Disposable;
import gloop.general.collections.GenericStackable;
import gloop.general.collections.Stack;
import gloop.graphics.cameras.Camera;
import gloop.graphics.data.models.*;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.posteffects.PostEffect;
import gloop.graphics.rendering.shading.posteffects.PostProcessor;
import gloop.graphics.rendering.texturing.*;
import org.lwjgl.opengl.Display;

import gloop.graphics.rendering.shading.ShaderManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Renderer implements Disposable {
	protected Scene scene = new Scene();
	protected static boolean UseDebugCamera = false;

	public abstract void bind(Renderer previoustechnique);
	public abstract void reset();
	protected abstract void renderScene();
	public abstract Texture getTexture();
	public abstract FrameBuffer getBuffer();
	public void setScene(Scene scene) { this.scene = scene; }
	public Scene getScene() { return scene; }
	@Override
	public void requestDisposal() {	dispose(); }


	private static ForwardRenderer ForwardRenderer;
	private static DeferredRenderer DeferedRenderer;
	private static Renderer CurrentRenderer = null;

	private static long LastFrame = System.currentTimeMillis();
	private static float Delta;
	private static float TimeScaler;
	private static boolean RenderWhenHidden = false;

	private static final List<PostEffect> POST_EFFECTS = new ArrayList<>();

	private static final Stack<CullFaceEnabledState> CULL_FACE_ENABLED_STACK = new Stack();
	private static final Stack<CullFaceState> CULL_FACE_STACK = new Stack();
	private static final Stack<BlendingEnabledState> BLENDING_ENABLED_STACK = new Stack();
	private static final Stack<DepthTestingEnabledState> DEPTH_TESTING_ENABLED_STACK = new Stack();
	private static final Stack<ScissorTestingEnabledState> SCISSOR_TESTING_ENABLED_STACK = new Stack();
	private static final Stack<DepthBufferWriteEnabledState> DEPTH_BUFFER_WRITE_ENABLED_STACK = new Stack();
	private static final Stack<ColorBufferWriteMaskState> COLOR_BUFFER_WRITE_MASK_STACK = new Stack();
	private static final Stack<StencilTestingEnabledState> STENCIL_TESTING_ENABLED_STACK = new Stack();
	private static final Stack<StencilBufferState> STENCIL_BUFFER_STATE_STACK = new Stack();
	private static final Stack<BlendFunctionsState> BLEND_FUNCTIONS_STATE_STACK = new Stack();

	private static CullingMethod CullingMethod = new FrustumCullingMethod();

	private static class GLEnabledState extends GenericStackable {
		protected int glEnum;
		private final boolean enabled;

		public GLEnabledState(int glenum, boolean enabled) {
			this.glEnum = glenum;
			this.enabled = enabled;
		}

		@Override
		public void enable() { setState(); }

		@Override
		public void disable() {}

		protected void setState() {
			if (enabled)
				glEnable(glEnum);
			else
				glDisable(glEnum);
		}

		public boolean isEnabled() { return enabled; }
	}
	private static class CullFaceEnabledState extends GLEnabledState {
		public CullFaceEnabledState(boolean enabled) {
			super(GL_CULL_FACE, enabled);
		}
	}
	private static class CullFaceState extends GenericStackable {
		private final gloop.graphics.rendering.CullFaceState state;

		public CullFaceState(gloop.graphics.rendering.CullFaceState value) {
			this.state = value;
		}

		@Override
		public void enable() { setState(); }

		@Override
		public void disable() {}

		protected void setState() {
			glCullFace(state.getGLEnum());
		}

		public gloop.graphics.rendering.CullFaceState getState() { return state; }
	}
	private static class BlendingEnabledState extends GLEnabledState {
		public BlendingEnabledState(boolean enabled) {
			super(GL_BLEND, enabled);
		}
	}
	private static class DepthTestingEnabledState extends GLEnabledState {
		public DepthTestingEnabledState(boolean enabled) {
			super(GL_DEPTH_TEST, enabled);
		}
	}
	private static class ScissorTestingEnabledState extends GLEnabledState {
		public ScissorTestingEnabledState(boolean enabled) {
			super(GL_SCISSOR_TEST, enabled);
		}
	}
	private static class StencilTestingEnabledState extends GLEnabledState {
		public StencilTestingEnabledState(boolean enabled) {
			super(GL_STENCIL_TEST, enabled);
		}
	}
	private static class DepthBufferWriteEnabledState extends GenericStackable {
		private final boolean enabled;
		public DepthBufferWriteEnabledState(boolean enabled) {
			this.enabled = enabled;
		}

		@Override
		public void enable() { glDepthMask(enabled); }

		@Override
		public void disable() {}

		public boolean isEnabled() { return enabled; }
	}
	private static class ColorBufferWriteMaskState extends GenericStackable {
		private final boolean red;
		private final boolean green;
		private final boolean blue;
		private final boolean alpha;
		public ColorBufferWriteMaskState(boolean red, boolean green, boolean blue, boolean alpha) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.alpha = alpha;
		}

		@Override
		public void enable() { glColorMask(red, green, blue, alpha); }

		@Override
		public void disable() {}
	}
	private static class StencilBufferState extends GenericStackable {
		private final Condition passCondition;
		private final int writeValue;
		private final int functionMask;
		public StencilBufferState(Condition passcondition, int writevalue, int functionmask) {
			passCondition = passcondition;
			writeValue = writevalue;
			functionMask = functionmask;
		}

		@Override
		public void enable() { glStencilFunc(passCondition.getGLEnum(), writeValue, functionMask); }

		@Override
		public void disable() {}
	}
	private static class BlendFunctionsState extends GenericStackable {
		private final BlendFunction sourceFunciton;
		private final BlendFunction destinationFunciton;

		public BlendFunctionsState(BlendFunction sourcefunction, BlendFunction destinationfunciton) {
			sourceFunciton = sourcefunction;
			destinationFunciton = destinationfunciton;
		}

		@Override
		public void enable() { glBlendFunc(sourceFunciton.getGLEnum(), destinationFunciton.getGLEnum()); }

		@Override
		public void disable() { }
	}
	// TODO: Add glStencilOp

	private static final CullFaceEnabledState ENABLED_CULL_FACE_STATE = new CullFaceEnabledState(true);
	private static final CullFaceEnabledState DISABLED_CULL_FACE_STATE = new CullFaceEnabledState(false);
	private static final CullFaceState FRONT_CULL_FACE_STATE = new CullFaceState(gloop.graphics.rendering.CullFaceState.Front);
	private static final CullFaceState BACK_CULL_FACE_STATE = new CullFaceState(gloop.graphics.rendering.CullFaceState.Back);
	private static final BlendingEnabledState ENABLED_BLENDING_STATE = new BlendingEnabledState(true);
	private static final BlendingEnabledState DISABLED_BLENDING_STATE = new BlendingEnabledState(false);
	private static final DepthTestingEnabledState ENABLED_DEPTH_TESTING_STATE = new DepthTestingEnabledState(true);
	private static final DepthTestingEnabledState DISABLED_DEPTH_TESTING_STATE = new DepthTestingEnabledState(false);
	private static final ScissorTestingEnabledState ENABLED_SCISSOR_TESTING_STATE = new ScissorTestingEnabledState(true);
	private static final ScissorTestingEnabledState DISABLED_SCISSOR_TESTING_STATE = new ScissorTestingEnabledState(false);
	private static final DepthBufferWriteEnabledState ENABLED_DEPTH_BUFFER_WRITE_STATE = new DepthBufferWriteEnabledState(true);
	private static final DepthBufferWriteEnabledState DISABLED_DEPTH_BUFFER_WRITE_STATE = new DepthBufferWriteEnabledState(false);
	private static final StencilTestingEnabledState ENABLE_STENCIL_TESTING_STATE = new StencilTestingEnabledState(true);
	private static final StencilTestingEnabledState DISABLED_STENCIL_TESTING_STATE = new StencilTestingEnabledState(false);

	static {
		// Set inital state
		enableBlending(false);
		enableDepthTesting(true);
		enableFaceCulling(true);
		setFaceCulling(gloop.graphics.rendering.CullFaceState.Back);
		enableDepthBufferWriting(true);
		enableColorBufferWriting(true, true, true, true);
		enableStencilTesting(true);
		setStencilBufferState(Condition.Always, 1, 0xFF);
		glStencilOp(GL_KEEP, GL_KEEP, GL_REPLACE); // TODO: Pull this out to public methods
		setDepthFunction(DepthFunction.Less);
		//setVoidColor(1,1,1);

		setBlendFunctionsState(BlendFunction.SourceAlpha, BlendFunction.OneMinusSourceAlpha); // TODO: Sure this is the default?
		glEnable(GL_FRAMEBUFFER_SRGB); // TODO: Pull this out to public methods

		setRenderer(getForwardRenderer());
	}

	public static void init() throws IOException {
		if (Settings.EnableShadows)
			Material.createShadowMapShader();
	}

	public static DeferredRenderer getDeferedRenderer() throws IOException, ShaderCompilationException {
		if (DeferedRenderer == null || DeferedRenderer.isDisposed())
			DeferedRenderer = new DeferredRenderer();
		return DeferedRenderer;
	}
	public static ForwardRenderer getForwardRenderer() {
		if (ForwardRenderer == null || ForwardRenderer.isDisposed())
			ForwardRenderer = new ForwardRenderer();
		return ForwardRenderer;
	}

	public static void useDebugCamera(boolean usedebugcamera) {
		UseDebugCamera = usedebugcamera;
	}

	public static Camera getCurrentCamera() {
		Scene scene = getRenderer().getScene();
		return (UseDebugCamera) ? scene.getDebugCamera() : scene.getGameCamera();
	}

	public static void setRenderer(Renderer newtechnique) {
		newtechnique.bind(CurrentRenderer);
		CurrentRenderer = newtechnique;
	}
	public static Renderer getRenderer() { return CurrentRenderer; }

	public static void setCullingMethod(CullingMethod method) {
		if (method != null)
			CullingMethod = method;
	}


	public static void addPostEffect(PostEffect effect){ POST_EFFECTS.add(effect); }

	public static void swapBuffers() {
		Texture finaltexture = CurrentRenderer.getTexture();

		if (PostMan.isActive()) {
			PostMan.render(finaltexture, POST_EFFECTS);
			finaltexture = PostMan.getResult();
		}

		FrameBuffer.bindDefault();
		Viewport.setDimensions(finaltexture.getWidth(), finaltexture.getHeight());
		PostProcessor.render(finaltexture);

		CurrentRenderer.bind(null);
	}

	public static void update() {
		//TODO: Maybe move these to Scene class

		// Sort for transparrency
		if (DeferedRenderer != null)
			sortModels(DeferedRenderer.getScene().getModels(), DeferedRenderer.getScene().getGameCamera());
		if (ForwardRenderer != null)
			sortModels(ForwardRenderer.getScene().getModels(), ForwardRenderer.getScene().getGameCamera());

		updateEnvironmentProbes();

		if (DeferedRenderer != null)
			updateShadowMaps(DeferedRenderer.getScene());
		if (ForwardRenderer != null)
			updateShadowMaps(ForwardRenderer.getScene());

		if (DeferedRenderer != null)
			CullingMethod.calculateSceneOcclusion(DeferedRenderer.getScene().getModels());
		if (ForwardRenderer != null)
			CullingMethod.calculateSceneOcclusion(ForwardRenderer.getScene().getModels());
	}

	private static void updateEnvironmentProbes() {
		//TODO: If both renderers use same scene and a probe is set to renew every frame, it will renew twice here
		if (DeferedRenderer != null)
			updateEnvironmentProbes(DeferedRenderer);
		if (ForwardRenderer != null)
			updateEnvironmentProbes(ForwardRenderer);
	}

	private static void updateEnvironmentProbes(Renderer renderer) {
		int numprobes = renderer.getScene().getNumberOfEnvironmentProbes();
		for (int i=0; i<numprobes; i++)
			renderer.getScene().getEnvironmentProbe(i).renew();
	}

	private static void updateShadowMaps(Scene scene) {
		for (int i=0; i<scene.getNumberOfSpotLights(); i++)
			scene.getSpotLight(i).updateShadowMap();
		for (int i=0; i<scene.getNumberOfPointLights(); i++)
			scene.getPointLight(i).updateShadowMap();
		for (int i=0; i<scene.getNumberOfDirectionalLights(); i++)
			scene.getDirectionallight(i).updateShadowMap();
	}

	//TODO: Only really need to sort transparrent objects by z
	private static void sortModels(List<Model3D> models, Camera cam) {
		Vector3f camerapos = new Vector3f();
		Vector3f objpos = new Vector3f();
		Vector3f diff = new Vector3f();

		cam.getPosition(camerapos);

		// Bubble sort
		for (int i=0; i<models.size()-1; i++) {
			boolean swapped = false;
			for (int j=i; j<models.size()-1; j++) {
				Model3D leftmodel = models.get(j);
				Model3D rightmodel = models.get(j+1);
				leftmodel.getPostition(objpos);
				Vector3f.sub(camerapos, objpos, diff);
				float leftobjdistfromcam = diff.length();
				rightmodel.getPostition(objpos);
				Vector3f.sub(camerapos, objpos, diff);
				float rightobjdistfromcam = diff.length();

				if (rightobjdistfromcam < leftobjdistfromcam)
					continue;

				// Swap
				models.set(j+1, leftmodel);
				models.set(j, rightmodel);
				swapped = true;
			}

			if (!swapped)
				return;
		}
	}

	public static void render() {
		if (!Display.isVisible())
			return;

		CurrentRenderer.renderScene();
	}

	public static void calculateSceneOcclusion() {

	}

	public static void enablePostEffects() {
		PostMan.init();
	}



	///////////////////////////////// GL States /////////////////////////////////
	public static void setStencilBufferState(Condition passcondition, int writevalue, int functionmask) {
		STENCIL_BUFFER_STATE_STACK.push(new StencilBufferState(passcondition, writevalue, functionmask));
	}
	public static void popStencilBufferState() { STENCIL_BUFFER_STATE_STACK.pop(); } //TODO: What if it underflows?

	public static void setBlendFunctionsState(BlendFunction sourcefunction, BlendFunction destinationfunction) { BLEND_FUNCTIONS_STATE_STACK.push(new BlendFunctionsState(sourcefunction, destinationfunction)); }
	public static void popBlendFunctionsState() { BLEND_FUNCTIONS_STATE_STACK.pop(); } //TODO: What if it underflows?

	public static void enableStencilTesting(boolean enabled) {
		STENCIL_TESTING_ENABLED_STACK.push(enabled ? ENABLE_STENCIL_TESTING_STATE : DISABLED_STENCIL_TESTING_STATE);
	}
	public static boolean popStencilTestingState() {
		StencilTestingEnabledState laststate = STENCIL_TESTING_ENABLED_STACK.pop();
		if (laststate == null)
			return false; // OpengGL's default
		return laststate.isEnabled();
	}

	public static void enableFaceCulling(boolean enabled) {
		CULL_FACE_ENABLED_STACK.push(enabled ? ENABLED_CULL_FACE_STATE : DISABLED_CULL_FACE_STATE);
	}
	public static boolean popFaceCullingEnabledState() {
		CullFaceEnabledState laststate = CULL_FACE_ENABLED_STACK.pop();
		if (laststate == null)
			return false; // OpengGL's default
		return laststate.isEnabled();
	}

	public static void setFaceCulling(gloop.graphics.rendering.CullFaceState newstate) {
		CULL_FACE_STACK.push((newstate == gloop.graphics.rendering.CullFaceState.Front) ? FRONT_CULL_FACE_STATE : BACK_CULL_FACE_STATE);
	}
	public static gloop.graphics.rendering.CullFaceState popFaceCullingState() {
		CullFaceState laststate = CULL_FACE_STACK.pop();
		if (laststate == null)
			return null;
		return laststate.state;
	}

	public static void enableBlending(boolean enabled) {
		BLENDING_ENABLED_STACK.push(enabled ? ENABLED_BLENDING_STATE : DISABLED_BLENDING_STATE);
	}
	public static boolean popBlendingEnabledState() {
		BlendingEnabledState laststate = BLENDING_ENABLED_STACK.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}

	/**
	 * Allows or blocks write access to the depth buffer
	 * @param enabled Whether to enable or disable writes to the depth buffer
	 */
	public static void enableDepthBufferWriting(boolean enabled) {
		DEPTH_BUFFER_WRITE_ENABLED_STACK.push(enabled ? ENABLED_DEPTH_BUFFER_WRITE_STATE : DISABLED_DEPTH_BUFFER_WRITE_STATE);
	}
	public static boolean popDepthBufferWritingState() {
		DepthBufferWriteEnabledState laststate = DEPTH_BUFFER_WRITE_ENABLED_STACK.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}

	public static void enableDepthTesting(boolean enabled) {
		DEPTH_TESTING_ENABLED_STACK.push(enabled ? ENABLED_DEPTH_TESTING_STATE : DISABLED_DEPTH_TESTING_STATE);
	}
	public static boolean popDepthTestingEnabledState() {
		DepthTestingEnabledState laststate = DEPTH_TESTING_ENABLED_STACK.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}

	/**
	 * Allows or blocks write access to each element of the color buffer
	 * @param red Enable writing to the red component
	 * @param green Enable writing to the green component
	 * @param blue Enable writing to the blue component
	 * @param alpha Enable writing to the alpha component
	 */
	public static void enableColorBufferWriting(boolean red, boolean green, boolean blue, boolean alpha) {
		COLOR_BUFFER_WRITE_MASK_STACK.push(new ColorBufferWriteMaskState(red, green, blue, alpha));
	}
	public static void popColorBufferWritingState() { COLOR_BUFFER_WRITE_MASK_STACK.pop(); }


	public static void enableScissorTesting(boolean enabled) {
		if (enabled)
			SCISSOR_TESTING_ENABLED_STACK.push(ENABLED_SCISSOR_TESTING_STATE);
		else
			SCISSOR_TESTING_ENABLED_STACK.push(DISABLED_SCISSOR_TESTING_STATE);
	}
	public static boolean popScissorTestingState() {
		ScissorTestingEnabledState laststate = SCISSOR_TESTING_ENABLED_STACK.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}
	public static void setScissorArea(int x, int y, int width, int height) { glScissor(x, y, width, height); }

	// TODO: EnableAlpha blending
	// TODO: SetAlphaBlendingFunction
	// TODO: Make this a stack
	public static void setVoidColor(float red, float green, float blue) {
		glClearColor(
				red,
				green,
				blue,
				0
		);
	}

	public static void setDepthFunction(DepthFunction function) { glDepthFunc(function.getGLEnum()); }
	//TODO: PopDepthFunction

	/** Allows the graphics of specified color components. */
	public static void setColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		glColorMask(red, green, blue, alpha);
	}

	public static void clear(boolean colorbuffer, boolean depthbuffer, boolean stencilbuffer) {
		int mask = getBufferBits(colorbuffer, depthbuffer, stencilbuffer);

		glClear(mask);
	}



	public static boolean willRenderWhenHidden() {
		return RenderWhenHidden;
	}
	public static void renderWhenHidden(boolean renderWhenHidden) {
		RenderWhenHidden = renderWhenHidden;
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

	public static float getTimeDelta() { return Delta; }
	public static float getTimeScaler() { return TimeScaler; }

	public static void checkErrors() {
		int errorcode;
		do {
			errorcode = GL11.glGetError();
			String errorinfo = null;
			switch (errorcode) {
				case GL11.GL_INVALID_ENUM:
					errorinfo = "An unexpected enum value has been used.";
					break;
				case GL11.GL_INVALID_VALUE:
					errorinfo = "An invalid value has been provided.";
					break;
				case GL11.GL_INVALID_OPERATION:
					errorinfo = "The operation is not allowed in the current state";
					break;
				case GL30.GL_INVALID_FRAMEBUFFER_OPERATION:
					errorinfo = "The framebuffer object is not complete";
					break;
				case GL11.GL_OUT_OF_MEMORY:
					errorinfo = "here is not enough memory left to execute the command";
					break;
				case GL11.GL_STACK_UNDERFLOW:
					errorinfo = "An attempt has been made to perform an operation that would cause an internal stack to underflow.";
					break;
				case GL11.GL_STACK_OVERFLOW:
					errorinfo = "An attempt has been made to perform an operation that would cause an internal stack to overflow.";
					break;
			}
			if (errorinfo != null) {
				System.err.println(errorinfo);
				System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()));
			}
		} while (errorcode != GL11.GL_NO_ERROR);
	}

	//TODO: Call privately from Update()
	public static void updateTimeDelta() {
		long now = System.nanoTime();
		Delta = (now - LastFrame) / 1000000f;
		TimeScaler = Delta / (1000f / Viewport.getFocusedFrameRate());
		LastFrame = now;
	}

	public static void disposeAll() {
		VertexBufferManager.cleanup();
		VertexArrayManager.cleanup();
		TextureManager.cleanup();
		FrameBufferManager.cleanup();
		ShaderManager.cleanup();

		if (ForwardRenderer != null)
			ForwardRenderer.dispose();
		if (DeferedRenderer != null)
			DeferedRenderer.dispose();
	}
}
