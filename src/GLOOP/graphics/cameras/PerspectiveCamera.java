package GLOOP.graphics.cameras;

import GLOOP.general.math.MathFunctions;
import GLOOP.graphics.rendering.Viewport;

public class PerspectiveCamera extends Camera {
	protected int width, height;
	protected float fov;

	public static final float
		DEFAULT_FOV = 70;

	public PerspectiveCamera() {
		this(Viewport.getWidth(), Viewport.getHeight(), DEFAULT_FOV, DEFAULT_ZNEAR, DEFAULT_ZFAR);
	}
	public PerspectiveCamera(int width, int height, float fov, float znear, float zfar) {
		this.width = width;
		this.height = height;
		this.fov = fov;
		this.znear = znear;
		this.zfar = zfar;

		updateProjectionMatrix();
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;

		updateProjectionMatrix();
	}

	@Override
	public void setznear(float znear) {
		super.setznear(znear);

		updateProjectionMatrix();
	}
	public float getznear() { return znear; }


	@Override
	public void setzfar(float zfar) {
		super.setzfar(zfar);

		updateProjectionMatrix();
	}
	public float getzfar() { return zfar; }

	public void setFov(float fov) {
		this.fov = fov;

		updateProjectionMatrix();
	}
	public float getFov() { return fov; }

	private void updateProjectionMatrix() {
		MathFunctions.createProjectionMatrix(width, height, fov, znear, zfar, ProjectionMatrix);
	}
}