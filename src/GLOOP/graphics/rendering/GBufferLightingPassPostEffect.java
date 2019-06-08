package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;

final class GBufferLightingPassPostEffect extends GBufferPostEffect<GBufferDeferredLightingPassShader> {
	private float volumetricLightsStrength = 2;

	public GBufferLightingPassPostEffect(GBufferDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	@Override
	public void commit() {
		super.commit();
		shader.updateLights();
		shader.setVolumetricLightsStrength(volumetricLightsStrength);
		shader.setTime(Viewport.getElapsedSeconds());
	}

	public void setVolumetricLightsStrength(float volumetriclightsstrength) { volumetricLightsStrength = volumetriclightsstrength; }
}
