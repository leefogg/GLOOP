package engine.graphics.cameras;

import engine.math.MathFunctions;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public abstract class Camera {
	public static final float
		DEFAULT_ZNEAR = 0.01F,
		DEFAULT_ZFAR = 1000;

	protected float znear, zfar;
	protected Matrix4f
		ProjectionMatrix = new Matrix4f(),
		ViewMatrix = new Matrix4f();
	protected Vector3f
		Position = new Vector3f(),
		Rotation = new Vector3f(); // TODO: Change to quaternion
	protected boolean viewMatrixIsDirty = true; // Flag to update the view matrix only once per frame

	public void update(float delta, float timescaler) {}

	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	public void setPosition(float x, float y, float z) {
		Position.set(x, y, z);

		viewMatrixIsDirty = true;
	}
	public void getPosition(Vector3f clone) {
		clone.set(Position);
	}

	public void setRotation(Vector3f rotation) {
		setRotation(rotation.x, rotation.y, rotation.z);
	}
	public void setRotation(float x, float y, float z) {
		Rotation.set(x, y, z);

		viewMatrixIsDirty = true;
	}

	public void  setznear(float znear) {
		this.znear = znear;
	}
	public float getznear() {
		return znear;
	}

	public void  setzfar(float zfar) {
		this.zfar = zfar;
	}
	public float getzfar() {
		return zfar;
	}

	// Note: this does not change Rotation field
	public void lookAt(Vector3f target) {
		MathFunctions.createViewMatrix(Position, target, new Vector3f(0,1,0), ViewMatrix);
		viewMatrixIsDirty = false;
	}

	public Matrix4f getProjectionMatrix() {
		return ProjectionMatrix;
	}

	public Matrix4f getViewMatrix() {
		if (viewMatrixIsDirty)
			ViewMatrix = MathFunctions.createViewMatrix(Position, Rotation, ViewMatrix);

		viewMatrixIsDirty = false;
		return ViewMatrix;
	}
}
