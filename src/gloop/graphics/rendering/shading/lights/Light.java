package gloop.graphics.rendering.shading.lights;

import gloop.graphics.rendering.texturing.Texture;

public abstract class Light {
	public abstract boolean isComplex();
	public abstract boolean isShadowMapEnabled();
	public abstract void enableShadows(int resolution, int refreshRate, float zfar);
	public abstract void disableShadows();
	public abstract void updateShadowMap();
	public abstract Texture getShadowMap();
}
