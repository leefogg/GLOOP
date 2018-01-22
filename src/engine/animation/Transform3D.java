package engine.animation;

import engine.math.MathFunctions;
import engine.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Transform3D {
	private final Matrix4f modelMatrix = new Matrix4f();

	private boolean transformationMatrixIsDirty = true;
	private final Vector3f
	postition = new Vector3f(0, 0, 0),
	scale = new Vector3f(1f, 1f, 1f);
	private final Quaternion Rotation = new Quaternion();

	public Matrix4f getModelMatrix() {
		if (isDirty()) {
			MathFunctions.createTransformationMatrix(postition, Rotation, scale, modelMatrix);
			transformationMatrixIsDirty = false;
		}

		return modelMatrix;
	}

	public Vector3f getPostition(Vector3f postition) {
		return postition.set(this.postition);
	}
	public void setPostition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	public void setPosition(float x, float y, float z) {
		postition.set(x, y, z);

		setDirty();
	}

	public Quaternion getRotation() {
		return Rotation;
	}
	public void setRotation(Quaternion rotation) {
		Rotation.set(rotation);

		setDirty();
	}

	public Vector3f getScale(Vector3f scale) {
		return scale.set(this.scale);
	}
	public void setScale(Vector3f scale) {
		setScale(scale.x, scale.y, scale.z);

		setDirty();
	}
	public void setScale(float width, float height, float depth) {
		scale.set(width, height, depth);

		setDirty();
	}

	private void setDirty() {
		transformationMatrixIsDirty = true;
	}
	private boolean isDirty() {
		return transformationMatrixIsDirty;
	}
}
