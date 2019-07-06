package GLOOP.graphics.rendering.shading.lights;

import org.lwjgl.util.vector.Vector3f;

public final class SpotLight extends Light {
	private final Vector3f position = new Vector3f(0,0,0);
	private final Vector3f direction = new Vector3f(0,-1,0); // TODO: Make Quaternion
	private final Vector3f color = new Vector3f(1,1,1);
	private float innerCone, outerCone;
	private float QuadraticAttenuation;

	public SpotLight() {
		setInnerCone(45);
		setOuterCone(90);
		setQuadraticAttenuation(0.01f);
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

	public float getQuadraticAttenuation() { return QuadraticAttenuation; }
	public void  setQuadraticAttenuation(float quadraticattenuation) { QuadraticAttenuation = quadraticattenuation; }

	public void lookAt(Vector3f point) { lookAt(point.x, point.y, point.z); }
	public void lookAt(float x, float y, float z) {
		direction.x = x - position.x;
		direction.y = y - position.y;
		direction.z = z - position.z;

		direction.normalise();
	}

	public void setPosition(Vector3f position) { setPosition(position.x, position.y, position.z); }
	public void setPosition(float x, float y, float z) { position.set(x,y,z); }
	public void setColor(Vector3f diffusecolor) { setColor(diffusecolor.x, diffusecolor.y, diffusecolor.z); }
	public void setColor(float r, float g, float b) { color.set(r, g, b); }
	public void setInnerCone(float innerconedegrees) { innerCone = (float)Math.cos(Math.toRadians(innerconedegrees)); }
	public void setOuterCone(float outerconedegrees) { outerCone = (float)Math.cos(Math.toRadians(outerconedegrees)); }

	@Override
	public boolean IsComplex() {
		return IsShadowMapEnabled();
	}

	@Override
	public boolean IsShadowMapEnabled() {
		return false;
	}

	@Override
	public void SetShadowMapEnabled(boolean enabled) {

	}

	@Override
	public void UpdateShadowMap() {

	}
}
