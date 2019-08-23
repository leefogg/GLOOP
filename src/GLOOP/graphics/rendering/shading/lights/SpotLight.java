package GLOOP.graphics.rendering.shading.lights;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.cameras.PerspectiveCamera;
import GLOOP.graphics.rendering.ForwardRenderer;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.texturing.FrameBuffer;
import GLOOP.graphics.rendering.texturing.PixelFormat;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureWrapMode;
import org.lwjgl.util.vector.Vector3f;

public final class SpotLight extends Light {
	private final Vector3f temp = new Vector3f();

	private final Vector3f position = new Vector3f(0,0,0);
	private final Vector3f direction = new Vector3f(0,-1,0); // TODO: Make Quaternion
	private final Vector3f color = new Vector3f(1,1,1);
	private float innerCone, outerCone;
	private float QuadraticAttenuation;

	private FrameBuffer shadowBuffer;
	public PerspectiveCamera renderCam;
	private int framesSinceShadowRender = 0;
	private int shadowRefreshFrequency = 1;

	public SpotLight() {
		setInnerCone(45);
		setOuterCone(90);
		setQuadraticAttenuation(0.01f);
	}

	public void getPosition(Vector3f destination) { destination.set(position); }
	public void getDirection(Vector3f destination) { destination.set(direction); }
	public void getColor(Vector3f destination) {
		destination.set(color);
	}
	public float getInnerCone() { return innerCone; }
	public float getOuterCone() { return outerCone; }

	public void setDirection(Vector3f direction) { setDirection(direction.x, direction.y, direction.z); }
	public void setDirection(float x, float y, float z) {this.direction.set(x, y, z); }

	public float getQuadraticAttenuation() { return QuadraticAttenuation; }
	public void  setQuadraticAttenuation(float quadraticattenuation) { QuadraticAttenuation = quadraticattenuation; }

	public void lookAt(Vector3f point) { lookAt(point.x, point.y, point.z); }
	public void lookAt(float x, float y, float z) {
		direction.x = x - position.x;
		direction.y = y - position.y;
		direction.z = z - position.z;

		direction.normalise();
	}

	public void setPosition(Vector3f position) { setPosition(position.x, position.y, position.z); }
	public void setPosition(float x, float y, float z) { position.set(x,y,z); }
	public void setColor(Vector3f diffusecolor) { setColor(diffusecolor.x, diffusecolor.y, diffusecolor.z); }
	public void setColor(float r, float g, float b) { color.set(r, g, b); }
	public void setInnerCone(float innerconedegrees) { innerCone = Math.min(outerCone, innerconedegrees); }
	public void setOuterCone(float outerconedegrees) { outerCone = Math.min(179f, outerconedegrees); }

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
		shadowBuffer = new FrameBuffer(resolution, resolution, PixelFormat.RGB16); // TODO: Use depth attachment
		Texture attachment = shadowBuffer.getColorTexture(0);
		attachment.setWrapMode(TextureWrapMode.BorderClamp);
		attachment.setBorderColor(1,1,1);
		renderCam = new PerspectiveCamera();
		renderCam.setzfar(zfar);
	}

	@Override
	public void disableShadows() {
		shadowBuffer.requestDisposal();
		renderCam = null;
	}

	@Override
	public void updateShadowMap() {
		if (!isShadowMapEnabled())
			return;
		framesSinceShadowRender++;
		if (framesSinceShadowRender != shadowRefreshFrequency)
			return;

		renderCam.setPosition(position);
		temp.set(
			position.x + direction.x,
			position.y + direction.y,
			position.z + direction.z
		);
		renderCam.lookAt(temp);
		renderCam.setFov(outerCone);

		FrameBuffer previousframebuffer = FrameBuffer.getCurrent();
		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Camera backupcam = renderer.getScene().getGameCamera();
		renderer.getScene().setGameCamera(renderCam);
		shadowBuffer.bind();

		renderer.reset();
		renderer.renderShadowScene();

		renderer.getScene().setGameCamera(backupcam);
		previousframebuffer.bind();

		framesSinceShadowRender = 0;
	}

	@Override
	public Texture getShadowMap() {
		return shadowBuffer.getColorTexture(0);
	}
}
