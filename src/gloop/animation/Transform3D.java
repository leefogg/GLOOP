package gloop.animation;

import gloop.general.math.MathFunctions;
import gloop.general.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Transform3D extends Transform<Vector3f>{
	private final Vector3f
		position = new Vector3f(0, 0, 0),
		scale = new Vector3f(1f, 1f, 1f);
	private final Quaternion rotation = new Quaternion();

	@Override
	public void getPosition(Vector3f out) { out.set(this.position); }
	@Override
	public void setPosition(Vector3f position) { setPosition(position.x, position.y, position.z); }
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);

		isDirty = true;
	}

	@Override
	public void getScale(Vector3f scale) { scale.set(this.scale); }
	@Override
	public void setScale(Vector3f scale) { setScale(scale.x, scale.y, scale.z);	}
	public void setScale(float width, float height, float depth) {
		scale.set(width, height, depth);

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
