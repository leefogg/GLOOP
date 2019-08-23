package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.CubeMap;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

class PointLightGBufferPostEffect extends GBufferPostEffect<PointLightDeferredLightingPassShader> {
	private static final Vector3f passthough = new Vector3f();

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
		light.getColor(passthough);
		shader.setColor(passthough);
		light.getPosition(passthough);
		shader.setPosition(passthough);
		shader.setQuadraticAttenuation(light.quadraticAttenuation);
		shader.setDepthMap(TextureUnit.EnvironmentMap);
		shader.setzFar(light.getzFar());
	}
}
