package gloop.animation;

import gloop.general.math.MathFunctions;
import gloop.general.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class Transform2D extends Transform<Vector2f> {
	private final Vector2f
		position = new Vector2f(0, 0),
		scale = new Vector2f(1f, 1f);
	private final Quaternion rotation = new Quaternion();

	@Override
	public void getPosition(Vector2f out) { out.set(position); }
	@Override
	public void setPosition(Vector2f position) {
		setPosition(position.x, position.y);
	}
	public void setPosition(float x, float y) {
		position.set(x, y);

		isDirty = true;
	}

	@Override
	public void getScale(Vector2f out) { out.set(scale); }
	@Override
	public void setScale(Vector2f scale) {
		setScale(scale.x, scale.y);
	}
	public void setScale(float width, float height) {
		scale.set(width, height);

		isDirty = true;
	}

	@Override
	public void getRotation(Quaternion out) { out.set(rotation); }
	@Override
	public void setRotation(Quaternion rotation) {
		this.rotation.set(rotation);

		isDirty = true;
	}

	@Override
	public void getModelMatrix(Matrix4f out) {
		if (isDirty) {
			MathFunctions.createTransformationMatrix(position, rotation, scale, modelMatrix);
			isDirty = false;
		}

		out.load(modelMatrix);
	}
}
