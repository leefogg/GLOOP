package gloop.graphics.cameras;

import gloop.general.math.MathFunctions;
import gloop.graphics.rendering.Viewport;
import org.lwjgl.util.vector.Matrix4f;

public class PerspectiveCamera extends Camera {
	protected int width, height;
	protected float fov;

	public static final float
		DEFAULT_FOV = 70;

	public PerspectiveCamera() {
		this(Viewport.getWidth(), Viewport.getHeight(), DEFAULT_FOV, DEFAULT_ZNEAR, DEFAULT_ZFAR);
	}
	public PerspectiveCamera(int width, int height, float fov, float znear, float zfar) {
		super();

		this.width = width;
		this.height = height;
		this.fov = fov;
		this.znear = znear;
		this.zfar = zfar;
	}

	public void setDimensions(int width, int height) {
		this.width = width;
		this.height = height;

		projectionMatrix.expire();
	}

	public float getznear() { return znear; }
	public float getzfar() { return zfar; }

	public void setFov(float fov) {
		this.fov = fov;

		projectionMatrix.expire();
	}
	public float getFov() { return fov; }

	@Override
	protected Matrix4f updateProjectionMatrix(Matrix4f projectionMatrix) {
		return MathFunctions.createProjectionMatrix(width, height, fov, znear, zfar, projectionMatrix);
	}
}
