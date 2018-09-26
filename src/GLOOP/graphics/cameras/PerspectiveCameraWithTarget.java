package GLOOP.graphics.cameras;

import GLOOP.graphics.rendering.Viewport;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class PerspectiveCameraWithTarget extends PerspectiveCamera {

	public PerspectiveCameraWithTarget() {
		this(Viewport.getWidth(), Viewport.getHeight(), DEFAULT_FOV, DEFAULT_ZNEAR, DEFAULT_ZFAR);
	}
	public PerspectiveCameraWithTarget(int width, int height, float fov, float znear, float zfar) {
		super(width, height, fov, znear, zfar);
	}

	public void setTarget(Vector3f target) {
		setTarget(target.x, target.y, target.z);
	}
	public void setTarget(float x, float y, float z) {
		Rotation.set(x, y, z); // Reuse
	}

	@Override
	public Matrix4f getViewMatrix() {
		lookAt(Rotation);

		return ViewMatrix;
	}
}
