package engine.graphics.shading.lighting;

import org.lwjgl.util.vector.Vector3f;

public final class SpotLight {
	private final Vector3f position = new Vector3f(0,0,0);
	private final Vector3f direction = new Vector3f(0,-1,0);
	private final Vector3f color = new Vector3f(1,1,1);
	private float innerCone, outerCone;
	public float linearAttenuation = Integer.MAX_VALUE,
				 quadraticAttenuation = Integer.MAX_VALUE;

	public SpotLight() {
		setInnerCone(45);
		setOuterCone(90);
	}

	public void getPosition(Vector3f destination) { destination.set(position); }
	public void getDirection(Vector3f destination) { destination.set(direction); }
	public void getColor(Vector3f destination) {
		destination.set(color);
	}
	public float getInnerCone() { return innerCone; }
	public float getOuterCone() { return outerCone; }

	public void setDirection(Vector3f direction) { setDirection(direction.x, direction.y, direction.z); }
	public void setDirection(float x, float y, float z) {this.direction.set(x, y, z); }

	public void pointAt(Vector3f point) { pointAt(point.x, point.y, point.z); }
	public void pointAt(float x, float y, float z) {
		direction.x -= x;
		direction.y -= y;
		direction.z -= z;

		direction.normalise();
	}

	public void setPosition(Vector3f position) { setPosition(position.x, position.y, position.z); }
	public void setPosition(float x, float y, float z) { position.set(x,y,z); }
	public void setColor(Vector3f diffusecolor) { setColor(diffusecolor.x, diffusecolor.y, diffusecolor.z); }
	public void setColor(float r, float g, float b) { color.set(r, g, b); }
	public void setInnerCone(float innerconedegrees) { innerCone = (float)Math.cos(Math.toRadians(innerconedegrees)); }
	public void setOuterCone(float outerconedegrees) { outerCone = (float)Math.cos(Math.toRadians(outerconedegrees)); }
}
