package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.lights.DirectionalLight;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.lights.SpotLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;

import java.util.List;

final class GBufferLightingPassPostEffect extends GBufferPostEffect<GBufferDeferredLightingPassShader> {
	private float volumetricLightsStrength = 2;
	private List<PointLight> pointLights;
	private List<SpotLight> spotLights;
	private List<DirectionalLight> directionalLights;

	public GBufferLightingPassPostEffect(GBufferDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void setSpotLights(List<SpotLight> lights) { spotLights = lights; }
	public void setPointLights(List<PointLight> lights) {	pointLights = lights; }
	public void setDirectionalLights(List<DirectionalLight> lights) { directionalLights = lights; }

	@Override
	public void commit() {
		super.commit();
		shader.updateLights(pointLights, spotLights, directionalLights);
		shader.setVolumetricLightsStrength(volumetricLightsStrength);
		shader.setTime(Viewport.getElapsedSeconds());
	}

	public void setVolumetricLightsStrength(float volumetriclightsstrength) { volumetricLightsStrength = volumetriclightsstrength; }
}
