package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.posteffects.GBufferPostEffect;
import gloop.graphics.rendering.texturing.CubeMap;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

class PointLightGBufferPostEffect extends GBufferPostEffect<PointLightDeferredLightingPassShader> {
	private static final Vector3f PASSTHOUGH = new Vector3f();

	private PointLight light = new PointLight();

	PointLightGBufferPostEffect(PointLightDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void set(PointLight light) {
		this.light = light;
	}

	@Override
	public void commit() {
		super.commit();

		CubeMap shadowMap = light.getShadowMap();
		TextureManager.bindReflectionMap(shadowMap);

		// light
		light.getColor(PASSTHOUGH);
		shader.setColor(PASSTHOUGH);
		light.getPosition(PASSTHOUGH);
		shader.setPosition(PASSTHOUGH);
		shader.setQuadraticAttenuation(light.quadraticAttenuation);
		shader.setDepthMap(TextureUnit.ENVIRONMENT_MAP);
		shader.setzFar(light.getzFar());
	}
}
