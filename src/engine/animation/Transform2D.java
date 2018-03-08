package engine.animation;

import engine.math.MathFunctions;
import engine.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class Transform2D extends Transform<Vector2f> {
	private final Vector2f
		Position = new Vector2f(0, 0),
		Scale = new Vector2f(1f, 1f);
	private final Quaternion Rotation = new Quaternion();

	@Override
	public void getPosition(Vector2f out) { out.set(Position); }
	@Override
	public void setPosition(Vector2f position) {
		setPosition(position.x, position.y);
	}
	public void setPosition(float x, float y) {
		Position.set(x, y);

		isDirty = true;
	}

	@Override
	public void getScale(Vector2f out) { out.set(Scale); }
	@Override
	public void setScale(Vector2f scale) {
		setScale(scale.x, scale.y);
	}
	public void setScale(float width, float height) {
		Scale.set(width, height);

		isDirty = true;
	}

	@Override
	public void getRotation(Quaternion out) { out.set(Rotation); }
	@Override
	public void setRotation(Quaternion rotation) {
		Rotation.set(rotation);

		isDirty = true;
	}

	@Override
	public void getTranslationMatrix(Matrix4f out) {
		MathFunctions.createTranslationMatrix(Position, out);
	}

	@Override
	public void getScaleMatrix(Matrix4f out) {
		MathFunctions.createScaleMatrix(Scale, out);
	}

	@Override
	public void getRotationMatrix(Matrix4f out) {
		MathFunctions.createRotatationMatrix(Rotation, out);
	}

	@Override
	public void getModelMatrix(Matrix4f out) {
		if (isDirty) {
			MathFunctions.createTransformationMatrix(Position, Rotation, Scale, modelMatrix);
			isDirty = false;
		}

		out.load(modelMatrix);
	}
}
