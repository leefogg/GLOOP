package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.lights.SpotLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Vector3f;

class SpotLightGBufferPostEffect extends GBufferPostEffect<SpotLightDeferredLightingPassShader> {
	private static final Vector3f passthough = new Vector3f();


	private SpotLight light = new SpotLight();
	private float volumetricLightsStrength;

	SpotLightGBufferPostEffect(SpotLightDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void set(SpotLight light) {
		this.light = light;
	}

	public void setVolumetricLightsStrength(float volumetriclightsstrength) { volumetricLightsStrength = volumetriclightsstrength; }

	@Override
	public void commit() {
		super.commit();

		// light
		light.getColor(passthough);
		shader.setColor(passthough);
		light.getDirection(passthough);
		shader.setDirection(passthough);
		light.getPosition(passthough);
		shader.setPosition(passthough);
		shader.setInnerConeAngle(light.getInnerCone());
		shader.setOuterConeAngle(light.getOuterCone());
		shader.setQuadraticAttenuation(light.getQuadraticAttenuation());
		shader.setVolumetricLightStrength(volumetricLightsStrength);
	}
}
