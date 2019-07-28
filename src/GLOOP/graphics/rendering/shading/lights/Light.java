package GLOOP.graphics.rendering.shading.lights;

import java.io.IOException;

public abstract class Light {
	public abstract boolean IsComplex();
	public abstract boolean isShadowMapEnabled();
	public abstract void SetShadowMapEnabled(boolean enabled) throws IOException;
	//public abstract void enableShadowMap(int resolution, int refreshRate, float zfar); //TODO: Replace above with this
	public abstract void RenderShadowMap();
}
