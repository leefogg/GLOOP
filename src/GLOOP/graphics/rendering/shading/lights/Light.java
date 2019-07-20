package GLOOP.graphics.rendering.shading.lights;

import java.io.IOException;

public abstract class Light {
	public abstract boolean IsComplex();
	public abstract boolean isShadowMapEnabled();
	public abstract void SetShadowMapEnabled(boolean enabled) throws IOException;
}
