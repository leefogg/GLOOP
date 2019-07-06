package GLOOP.graphics.rendering.shading.lights;

import org.lwjgl.util.vector.Vector3f;

public final class DirectionalLight extends Light {
	private final Vector3f direction = new Vector3f(0,0,0);
	private final Vector3f diffuseColor = new Vector3f(1,1,1);

	public final void getDirection(Vector3f destination) { destination.set(direction); }
	public final void getDiffuseColor(Vector3f destination) {
		destination.set(diffuseColor);
	}
	public final float getStrength() { return this.direction.length(); }

	public void setDirection(float x, float y, float z) { setDirection(new Vector3f(x, y, z)); }
	public void setDirection(Vector3f direction) {
		direction.negate(); // Optimisation. Instead of the pixel shader negating it for each pixel
		this.direction.set(direction);
	}
	public void setDiffuseColor(float r, float g, float b) { diffuseColor.set(r,g,b); }
	public void setDiffuseColor(Vector3f diffusecolor) { setDiffuseColor(diffusecolor.x, diffusecolor.y, diffusecolor.z); }
	public void setStrength(float strength) {
		this.direction.normalise();
		this.direction.scale(strength);
	}

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
