package GLOOP.graphics.rendering.shading.lights;

import org.lwjgl.util.vector.Vector3f;

public final class AmbientLight {
	private final Vector3f color = new Vector3f();

	public AmbientLight() {
		//setColor(new Vector3f(1,1,1));
	}

	public final void getColor(Vector3f destination) { destination.set(color); }

	public final void setColor(Vector3f color) {
		setColor(color.x, color.y, color.z);
	}
	public final void setColor(float r, float g, float b) {
		this.color.set(r,g,b);
	}
}
