package engine.animation;

import engine.math.MathFunctions;
import engine.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class Transform2D {
	private final Matrix4f modelMatrix = new Matrix4f();

	private boolean transformationMatrixIsDirty = true;
	private final Vector2f
	postition = new Vector2f(0, 0),
	scale = new Vector2f(1f, 1f);
	private final Quaternion Rotation = new Quaternion();

	public Matrix4f getModelMatrix() {
		if (isDirty()) {
			MathFunctions.createTransformationMatrix(postition, Rotation, scale, modelMatrix);
			transformationMatrixIsDirty = false;
		}

		return modelMatrix;
	}

	public Vector2f getPostition(Vector2f position) {
		return position.set(this.postition);
	}
	public void setPostition(Vector2f position) {
		setPosition(position.x, position.y);
	}
	public void setPosition(float x, float y) {
		postition.set(x, y);

		setDirty();
	}

	public Quaternion getRotation() {
		return Rotation;
	}
	public void setRotation(Quaternion rotation) {
		Rotation.set(rotation);
		setDirty();
	}

	public Vector2f getScale(Vector2f scale) {
		return scale.set(this.scale);
	}
	public void setScale(Vector2f scale) {
		setScale(scale.x, scale.y);
	}
	public void setScale(float width, float height) {
		scale.set(width, height);

		setDirty();
	}

	private void setDirty() {
		transformationMatrixIsDirty = true;
	}
	private boolean isDirty() {
		return transformationMatrixIsDirty;
	}
}
