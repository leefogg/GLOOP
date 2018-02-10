package engine.graphics.shading.lighting;

import org.lwjgl.util.vector.Vector3f;

public final class PointLight {
	private final Vector3f position = new Vector3f();
	private final Vector3f color = new Vector3f(1,1,1);
	public float quadraticAttenuation = Integer.MAX_VALUE;

	public final Vector3f getPosition(Vector3f destination) {
		return destination.set(position);
	}

	public final void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	public final void setPosition(float x, float y, float z) {
		position.set(x, y, z);
	}

	public final Vector3f getColor(Vector3f dest) {
		dest.set(color);
		return dest;
	}

	public final void setColor(Vector3f color) { setColor(color.x, color.y, color.z); }
	public final void setColor(float r, float g, float b) {
		color.set(r,g,b);
		color.normalise();
	}
}
