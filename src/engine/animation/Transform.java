package engine.animation;

import engine.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;

public abstract class Transform<T> {
	protected Matrix4f modelMatrix = new Matrix4f();
	protected boolean isDirty;

	public abstract void setPosition(T position);
	public abstract void getPosition(T out);
	public abstract void setScale(T scale);
	public abstract void getScale(T out);
	public abstract void setRotation(Quaternion rotation);
	public abstract void getRotation(Quaternion out);

	public abstract void getTranslationMatrix(Matrix4f out);
	public abstract void getScaleMatrix(Matrix4f out);
	public abstract void getRotationMatrix(Matrix4f out);
	public abstract void getModelMatrix(Matrix4f out);
}
