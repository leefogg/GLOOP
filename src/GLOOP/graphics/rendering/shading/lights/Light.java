package GLOOP.graphics.rendering.shading.lights;

public abstract class Light {
	public abstract boolean IsComplex();
	public abstract boolean IsShadowMapEnabled();
	public abstract void SetShadowMapEnabled(boolean enabled);
	public abstract void UpdateShadowMap();
}
