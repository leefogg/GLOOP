package GLOOP.graphics.rendering.shading.lights;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.cameras.OrthographicCamera;
import GLOOP.graphics.rendering.ForwardRenderer;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.texturing.FrameBuffer;
import GLOOP.graphics.rendering.texturing.PixelFormat;
import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Vector3f;

public final class DirectionalLight extends Light {
	private final Vector3f direction = new Vector3f(0,0,0);
	private final Vector3f diffuseColor = new Vector3f(1,1,1);
	public OrthographicCamera renderCam;
	private FrameBuffer shadowBuffer;
	private int FramesSinceShadowRender = 0;
	private int ShadowRefreshFrequency = 1;

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
		direction.negate(); // Optimisation. Instead of the pixel shader negating it for each pixel
		this.direction.set(direction);
	}
	public void setDiffuseColor(float r, float g, float b) { diffuseColor.set(r,g,b); }
	public void setDiffuseColor(Vector3f diffusecolor) { setDiffuseColor(diffusecolor.x, diffusecolor.y, diffusecolor.z); }
	public void setStrength(float strength) {
		this.direction.normalise();
		this.direction.scale(strength);
	}

	@Override
	public boolean IsComplex() {
		return isShadowMapEnabled();
	}

	@Override
	public boolean isShadowMapEnabled() {
		return shadowBuffer != null && !shadowBuffer.isDisposed();
	}

	@Override
	public void SetShadowMapEnabled(boolean enabled) {
		if (isShadowMapEnabled() == enabled)
			return;

		if (enabled) {
			shadowBuffer = new FrameBuffer(1024,1024, PixelFormat.R8);
			renderCam = new OrthographicCamera(40,40,0.1f,150);
			renderCam.setPosition(62,31,26);
			renderCam.setRotation(29,59,0);
		} else {
			shadowBuffer.requestDisposal();
			shadowBuffer = null;
		}
	}

	@Override
	public void RenderShadowMap() {
		FramesSinceShadowRender++;
		if (FramesSinceShadowRender != ShadowRefreshFrequency)
			return;

		FrameBuffer previousframebuffer = FrameBuffer.getCurrent();
		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Camera backupcam = renderer.getScene().getGameCamera();
		renderer.getScene().setGameCamera(renderCam);
		recalibrateCamera(backupcam);
		shadowBuffer.bind();

		renderer.reset();
		renderer.renderShadowScene();

		renderer.getScene().setGameCamera(backupcam);
		previousframebuffer.bind();

		FramesSinceShadowRender = 0;
	}

	private void recalibrateCamera(Camera gamecam) {

	}

	public Texture getShadowMap() {
		return shadowBuffer.getColorTexture(0);
	}
}
