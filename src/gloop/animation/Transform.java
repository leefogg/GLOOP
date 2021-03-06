package gloop.animation;

import gloop.general.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;

public abstract class Transform<T> {
	protected Matrix4f modelMatrix = new Matrix4f();
	protected boolean isDirty = true; // Force any caching to load on first use

	public abstract void setPosition(T position);
	public abstract void getPosition(T out);
	public abstract void setScale(T scale);
	public abstract void getScale(T out);
	public abstract void setRotation(Quaternion rotation);
	public abstract void getRotation(Quaternion out);
	
	public abstract void getModelMatrix(Matrix4f out);
}
