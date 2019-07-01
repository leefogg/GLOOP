package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Vector3f;

class FogGBufferPostEffect extends GBufferPostEffect<FogDeferredLightingPassShader> {
	private float fogDensity;
	private Vector3f fogColor = new Vector3f();

	FogGBufferPostEffect(FogDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void setFogDensity(float density) { fogDensity = density; }
	public void setFogColor(Vector3f color) { fogColor.set(color); }

	@Override
	public void commit() {
		super.commit();

		shader.setFogFactor(fogDensity);
		shader.setFogColor(fogColor);
	}
}
