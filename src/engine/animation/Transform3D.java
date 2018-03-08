package engine.animation;

import engine.math.MathFunctions;
import engine.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Transform3D extends Transform<Vector3f>{
	private final Vector3f
		Position = new Vector3f(0, 0, 0),
		Scale = new Vector3f(1f, 1f, 1f);
	private final Quaternion Rotation = new Quaternion();

	@Override
	public void getPosition(Vector3f out) { out.set(this.Position); }
	@Override
	public void setPosition(Vector3f position) { setPosition(position.x, position.y, position.z); }
	public void setPosition(float x, float y, float z) {
		Position.set(x, y, z);

		isDirty = true;
	}

	@Override
	public void getScale(Vector3f scale) { scale.set(this.Scale); }
	@Override
	public void setScale(Vector3f scale) { setScale(scale.x, scale.y, scale.z);	}
	public void setScale(float width, float height, float depth) {
		Scale.set(width, height, depth);

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
