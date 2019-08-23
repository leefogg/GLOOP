package GLOOP.graphics.rendering.shading.lights;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.cameras.OrthographicCamera;
import GLOOP.graphics.rendering.ForwardRenderer;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.texturing.FrameBuffer;
import GLOOP.graphics.rendering.texturing.PixelFormat;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.physics.data.AABB;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public final class DirectionalLight extends Light {
	private static final AABB FRUSTUM_AABB = new AABB(0,0,0,0,0,0);
	private static final Vector3f[] FRUSTUM_VERTS = new Vector3f[8];
	private static final Vector3f FRUSTUM_CENTER = new Vector3f();
	private static final Vector3f CAMERAPOSITION = new Vector3f();
	private static final Vector4f TEMPVECTOR = new Vector4f();

	static {
		for (int i = 0; i< FRUSTUM_VERTS.length; i++)
			FRUSTUM_VERTS[i] = new Vector3f();
	}

	private final Vector3f direction = new Vector3f(0,0,0);
	private final Vector3f diffuseColor = new Vector3f(1,1,1);
	public OrthographicCamera renderCam;
	private FrameBuffer shadowBuffer;
	private int framesSinceShadowRender = 0;
	private int shadowRefreshFrequency = 1;

	public DirectionalLight() {}
	public DirectionalLight(Vector3f direction) {
		this.direction.set(direction);
	}

	public final void getDirection(Vector3f destination) { destination.set(direction); }
	public final void getDiffuseColor(Vector3f destination) {
		destination.set(diffuseColor);
	}
	public final float getStrength() { return this.direction.length(); }

	public void setDirection(float x, float y, float z) { setDirection(new Vector3f(x, y, z)); }
	public void setDirection(Vector3f direction) {
		direction.normalise(this.direction);
	}
	public void setDiffuseColor(float r, float g, float b) { diffuseColor.set(r,g,b); }
	public void setDiffuseColor(Vector3f diffusecolor) { setDiffuseColor(diffusecolor.x, diffusecolor.y, diffusecolor.z); }
	public void setStrength(float strength) {
		this.direction.normalise();
		this.direction.scale(strength);
	}

	@Override
	public boolean isComplex() {
		return isShadowMapEnabled();
	}

	@Override
	public boolean isShadowMapEnabled() {
		return shadowBuffer != null && !shadowBuffer.isDisposed();
	}

	@Override
	public void enableShadows(int resolution, int refreshRate, float zfar) {
		shadowRefreshFrequency = refreshRate;
		// zFar is recallibrated each frame
		shadowBuffer = new FrameBuffer(resolution,resolution, PixelFormat.R8);
		renderCam = new OrthographicCamera(40,40,0.1f,150);
	}

	@Override
	public void disableShadows() {
		shadowBuffer.requestDisposal();
		shadowBuffer = null;
	}

	@Override
	public void updateShadowMap() {
		if (!isShadowMapEnabled())
			return;
		framesSinceShadowRender++;
		if (framesSinceShadowRender != shadowRefreshFrequency)
			return;

		FrameBuffer previousframebuffer = FrameBuffer.getCurrent();
		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Camera backupcam = renderer.getScene().getGameCamera();
		recalibrateCamera(backupcam);
		renderer.getScene().setGameCamera(renderCam);
		shadowBuffer.bind();

		renderer.reset();
		renderer.renderShadowScene();

		renderer.getScene().setGameCamera(backupcam);
		previousframebuffer.bind();

		framesSinceShadowRender = 0;
	}

	private void recalibrateCamera(Camera camera) {
		camera.getFrustumVerts(FRUSTUM_VERTS);

		camera.getPosition(CAMERAPOSITION);
		camera.getViewDirection(TEMPVECTOR);
		float halfzfar = camera.getzfar() / 2f;
		FRUSTUM_CENTER.set(
			CAMERAPOSITION.x - TEMPVECTOR.x * halfzfar,
			CAMERAPOSITION.y - TEMPVECTOR.y * halfzfar,
			CAMERAPOSITION.z - TEMPVECTOR.z * halfzfar
		);

		// Spin frustum to simulate AABB being aligned to camera direction
		// Approximate location
		renderCam.setPosition(
			FRUSTUM_CENTER.x + direction.x * 75,
			FRUSTUM_CENTER.y + direction.y * 75,
			FRUSTUM_CENTER.z + direction.z * 75
		);
		Matrix4f viewmatrix = camera.getViewMatrix();
		for (Vector3f vert : FRUSTUM_VERTS) {
			TEMPVECTOR.set(vert.x, vert.y, vert.z, 1);
			Matrix4f.transform(viewmatrix, TEMPVECTOR, TEMPVECTOR);
			vert.set(TEMPVECTOR);
		}

		float backPadding = 25;
		AABB.create(FRUSTUM_VERTS, FRUSTUM_AABB);
		renderCam.setSize(FRUSTUM_AABB.width, FRUSTUM_AABB.height);
		renderCam.setzfar(FRUSTUM_AABB.depth + backPadding);

		renderCam.setPosition(
			FRUSTUM_CENTER.x - direction.x * ((FRUSTUM_AABB.depth / 2f) + backPadding),
			FRUSTUM_CENTER.y - direction.y * ((FRUSTUM_AABB.depth / 2f) + backPadding),
			FRUSTUM_CENTER.z - direction.z * ((FRUSTUM_AABB.depth / 2f) + backPadding)
		);
		renderCam.lookAt(FRUSTUM_CENTER);
	}

	@Override
	public Texture getShadowMap() {
		return shadowBuffer.getColorTexture(0);
	}
}
