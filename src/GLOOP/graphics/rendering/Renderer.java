package GLOOP.graphics.rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_SRGB;

import GLOOP.graphics.Settings;
import GLOOP.graphics.rendering.shading.materials.Material;
import GLOOP.resources.Disposable;
import GLOOP.general.collections.GenericStackable;
import GLOOP.general.collections.Stack;
import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.data.models.*;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.posteffects.PostEffect;
import GLOOP.graphics.rendering.shading.posteffects.PostProcessor;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.opengl.Display;

import GLOOP.graphics.rendering.shading.ShaderManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Renderer implements Disposable {
	protected Scene scene = new Scene();
	protected static boolean useDebugCamera = false;

	public abstract void bind(Renderer previoustechnique);
	public abstract void reset();
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

	private static final ArrayList<PostEffect> postEffects = new ArrayList<>();

	private static final Stack<CullFaceEnabledState> CullFaceEnabledStack = new Stack();
	private static final Stack<CullFaceState> CullFaceStack = new Stack();
	private static final Stack<BlendingEnabledState> BlendingEnabledStack = new Stack();
	private static final Stack<DepthTestingEnabledState> DepthTestingEnabledStack = new Stack();
	private static final Stack<ScissorTestingEnabledState> ScissorTestingEnabledStack = new Stack();
	private static final Stack<DepthBufferWriteEnabledState> DepthBufferWriteEnabledStack = new Stack();
	private static final Stack<ColorBufferWriteMaskState> ColorBufferWriteMaskStack = new Stack();
	private static final Stack<StencilTestingEnabledState> StencilTestingEnabledStack = new Stack();
	private static final Stack<StencilBufferState> StencilBufferStateStack = new Stack();
	private static final Stack<BlendFunctionsState> BlendFunctionsStateStack = new Stack();

	private static CullingMethod CullingMethod = new FrustumCullingMethod();

	private static class GLEnabledState extends GenericStackable {
		protected int glEnum;
		private boolean enabled;

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
		private GLOOP.graphics.rendering.CullFaceState state;

		public CullFaceState(GLOOP.graphics.rendering.CullFaceState value) {
			this.state = value;
		}

		@Override
		public void enable() { setState(); }

		@Override
		public void disable() {}

		protected void setState() {
			glCullFace(state.getGLEnum());
		}

		public GLOOP.graphics.rendering.CullFaceState getState() { return state; }
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
		private boolean enabled;
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
		private boolean red, green, blue, alpha;
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
		private Condition PassCondition;
		private int WriteValue, FunctionMask;
		public StencilBufferState(Condition passcondition, int writevalue, int functionmask) {
			PassCondition = passcondition;
			WriteValue = writevalue;
			FunctionMask = functionmask;
		}

		@Override
		public void enable() { glStencilFunc(PassCondition.getGLEnum(), WriteValue, FunctionMask); }

		@Override
		public void disable() {}
	}
	private static class BlendFunctionsState extends GenericStackable {
		private BlendFunction SourceFunciton, DestinationFunciton;

		public BlendFunctionsState(BlendFunction sourcefunction, BlendFunction destinationfunciton) {
			SourceFunciton = sourcefunction;
			DestinationFunciton = destinationfunciton;
		}

		@Override
		public void enable() { glBlendFunc(SourceFunciton.GetGLEnum(), DestinationFunciton.GetGLEnum()); }

		@Override
		public void disable() { }
	}
	// TODO: Add glStencilOp

	private static final CullFaceEnabledState EnabledCullFaceState = new CullFaceEnabledState(true);
	private static final CullFaceEnabledState DisabledCullFaceState = new CullFaceEnabledState(false);
	private static final CullFaceState FrontCullFaceState = new CullFaceState(GLOOP.graphics.rendering.CullFaceState.Front);
	private static final CullFaceState BackCullFaceState = new CullFaceState(GLOOP.graphics.rendering.CullFaceState.Back);
	private static final BlendingEnabledState EnabledBlendingState = new BlendingEnabledState(true);
	private static final BlendingEnabledState DisabledBlendingState = new BlendingEnabledState(false);
	private static final DepthTestingEnabledState EnabledDepthTestingState = new DepthTestingEnabledState(true);
	private static final DepthTestingEnabledState DisabledDepthTestingState = new DepthTestingEnabledState(false);
	private static final ScissorTestingEnabledState EnabledScissorTestingState = new ScissorTestingEnabledState(true);
	private static final ScissorTestingEnabledState DisabledScissorTestingState = new ScissorTestingEnabledState(false);
	private static final DepthBufferWriteEnabledState EnabledDepthBufferWriteState = new DepthBufferWriteEnabledState(true);
	private static final DepthBufferWriteEnabledState DisabledDepthBufferWriteState = new DepthBufferWriteEnabledState(false);
	private static final StencilTestingEnabledState EnableStencilTestingState = new StencilTestingEnabledState(true);
	private static final StencilTestingEnabledState DisabledStencilTestingState = new StencilTestingEnabledState(false);

	static {
		// Set inital state
		enableBlending(false);
		enableDepthTesting(true);
		enableFaceCulling(true);
		setFaceCulling(GLOOP.graphics.rendering.CullFaceState.Back);
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

	public static void Init() throws IOException {
		if (Settings.EnableShadows)
			Material.CreateShadowMapShader();
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

	public static void useDebugCamera(boolean usedebugcamera) {
		useDebugCamera = usedebugcamera;
	}

	public static Camera getCurrentCamera() {
		Scene scene = getRenderer().getScene();
		return (useDebugCamera) ? scene.getDebugCamera() : scene.getGameCamera();
	}

	public static void setRenderer(Renderer newtechnique) {
		newtechnique.bind(currentRenderer);
		currentRenderer = newtechnique;
	}
	public static Renderer getRenderer() { return currentRenderer; }

	public static void setCullingMethod(CullingMethod method) {
		if (method != null)
			CullingMethod = method;
	}


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

	public static void update() {
		//TODO: Maybe move these to Scene class

		// Sort for transparrency
		if (deferedRenderer != null)
			sortModels(deferedRenderer.getScene().getModels(), deferedRenderer.getScene().getGameCamera());
		if (forwardRenderer != null)
			sortModels(forwardRenderer.getScene().getModels(), forwardRenderer.getScene().getGameCamera());

		updateEnvironmentProbes();

		if (deferedRenderer != null)
			updateShadowMaps(deferedRenderer.getScene());
		if (forwardRenderer != null)
			updateShadowMaps(forwardRenderer.getScene());

		if (deferedRenderer != null)
			CullingMethod.calculateSceneOcclusion(deferedRenderer.getScene().getModels());
		if (forwardRenderer != null)
			CullingMethod.calculateSceneOcclusion(forwardRenderer.getScene().getModels());
	}

	private static void updateEnvironmentProbes() {
		//TODO: If both renderers use same scene and a probe is set to renew every frame, it will renew twice here
		if (deferedRenderer != null)
			updateEnvironmentProbes(deferedRenderer);
		if (forwardRenderer != null)
			updateEnvironmentProbes(forwardRenderer);
	}

	private static void updateEnvironmentProbes(Renderer renderer) {
		int numprobes = renderer.getScene().GetNumberOfEnvironmentProbes();
		for (int i=0; i<numprobes; i++)
			renderer.getScene().GetEnvironmentProbe(i).renew();
	}

	private static void updateShadowMaps(Scene scene) {
		for (int i=0; i<scene.getNumberOfSpotLights(); i++)
			scene.getSpotLight(i).RenderShadowMap();
		for (int i=0; i<scene.getNumberOfPointLights(); i++)
			scene.getPointLight(i).RenderShadowMap();
		for (int i=0; i<scene.getNumberOfDirectionalLights(); i++)
			scene.getDirectionallight(i).RenderShadowMap();
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

		currentRenderer.renderScene();
	}

	public static final void calculateSceneOcclusion() {

	}

	public static void enablePostEffects() {
		PostMan.init();
	}



	///////////////////////////////// GL States /////////////////////////////////
	public static void setStencilBufferState(Condition passcondition, int writevalue, int functionmask) {
		StencilBufferStateStack.push(new StencilBufferState(passcondition, writevalue, functionmask));
	}
	public static void popStencilBufferState() { StencilBufferStateStack.pop(); } //TODO: What if it underflows?

	public static void setBlendFunctionsState(BlendFunction sourcefunction, BlendFunction destinationfunction) { BlendFunctionsStateStack.push(new BlendFunctionsState(sourcefunction, destinationfunction)); }
	public static void popBlendFunctionsState() { BlendFunctionsStateStack.pop(); } //TODO: What if it underflows?

	public static void enableStencilTesting(boolean enabled) {
		StencilTestingEnabledStack.push(enabled ? EnableStencilTestingState : DisabledStencilTestingState);
	}
	public static boolean popStencilTestingState() {
		StencilTestingEnabledState laststate = StencilTestingEnabledStack.pop();
		if (laststate == null)
			return false; // OpengGL's default
		return laststate.isEnabled();
	}

	public static void enableFaceCulling(boolean enabled) {
		CullFaceEnabledStack.push(enabled ? EnabledCullFaceState : DisabledCullFaceState);
	}
	public static boolean popFaceCullingEnabledState() {
		CullFaceEnabledState laststate = CullFaceEnabledStack.pop();
		if (laststate == null)
			return false; // OpengGL's default
		return laststate.isEnabled();
	}

	public static void setFaceCulling(GLOOP.graphics.rendering.CullFaceState newstate) {
		CullFaceStack.push((newstate == GLOOP.graphics.rendering.CullFaceState.Front) ? FrontCullFaceState : BackCullFaceState);
	}
	public static GLOOP.graphics.rendering.CullFaceState popFaceCullingState() {
		CullFaceState laststate = CullFaceStack.pop();
		if (laststate == null)
			return null;
		return laststate.state;
	}

	public static void enableBlending(boolean enabled) {
		BlendingEnabledStack.push(enabled ? EnabledBlendingState : DisabledBlendingState);
	}
	public static boolean popBlendingEnabledState() {
		BlendingEnabledState laststate = BlendingEnabledStack.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}

	/**
	 * Allows or blocks write access to the depth buffer
	 * @param enabled Whether to enable or disable writes to the depth buffer
	 */
	public static void enableDepthBufferWriting(boolean enabled) {
		DepthBufferWriteEnabledStack.push(enabled ? EnabledDepthBufferWriteState : DisabledDepthBufferWriteState);
	}
	public static boolean popDepthBufferWritingState() {
		DepthBufferWriteEnabledState laststate = DepthBufferWriteEnabledStack.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}

	public static void enableDepthTesting(boolean enabled) {
		DepthTestingEnabledStack.push(enabled ? EnabledDepthTestingState : DisabledDepthTestingState);
	}
	public static boolean popDepthTestingEnabledState() {
		DepthTestingEnabledState laststate = DepthTestingEnabledStack.pop();
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
		ColorBufferWriteMaskStack.push(new ColorBufferWriteMaskState(red, green, blue, alpha));
	}
	public static void popColorBufferWritingState() { ColorBufferWriteMaskStack.pop(); }


	public static void enableScissorTesting(boolean enabled) {
		if (enabled)
			ScissorTestingEnabledStack.push(EnabledScissorTestingState);
		else
			ScissorTestingEnabledStack.push(DisabledScissorTestingState);
	}
	public static boolean popScissorTestingState() {
		ScissorTestingEnabledState laststate = ScissorTestingEnabledStack.pop();
		if (laststate == null)
			return false; // OpenGL's default
		return laststate.isEnabled();
	}
	public static final void setScissorArea(int x, int y, int width, int height) { glScissor(x, y, width, height); }

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



	public static final boolean willRenderWhenHidden() {
		return RenderWhenHidden;
	}
	public static final void RenderWhenHidden(boolean renderWhenHidden) {
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

	public static final float getTimeDelta() { return Delta; }
	public static final float getTimeScaler() { return timeScaler; }

	public static final void checkErrors() {
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
		Delta = (now - lastFrame) / 1000000f;
		timeScaler = Delta / (1000f / Viewport.getFocusedFrameRate());
		lastFrame = now;
	}

	public static void disposeAll() {
		VertexBufferManager.cleanup();
		VertexArrayManager.cleanup();
		TextureManager.cleanup();
		FrameBufferManager.cleanup();
		ShaderManager.cleanup();

		if (forwardRenderer != null)
			forwardRenderer.dispose();
		if (deferedRenderer != null)
			deferedRenderer.dispose();
	}
}
