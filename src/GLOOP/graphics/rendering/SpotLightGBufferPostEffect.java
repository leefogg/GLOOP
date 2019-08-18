package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.lights.SpotLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

class SpotLightGBufferPostEffect extends GBufferPostEffect<SpotLightDeferredLightingPassShader> {
	private static final Vector3f passthough = new Vector3f();
	private static Matrix4f passthroughVPMatrix = new Matrix4f();

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

		Matrix4f viewmatrix = light.renderCam.getViewMatrix();
		Matrix4f projectionmatrix = light.renderCam.getProjectionMatrix();
		Matrix4f.mul(projectionmatrix, viewmatrix, passthroughVPMatrix);
		shader.setShadowCameraVPMatrix(passthroughVPMatrix);
		shader.setShadowMapZFar(light.renderCam.getzfar());
		TextureManager.bindAlbedoMap(light.getShadowMap());
		shader.setShadowMapTextureUnit(TextureUnit.AlbedoMap);

		// light
		light.getColor(passthough);
		shader.setColor(passthough);
		light.getDirection(passthough);
		shader.setDirection(passthough);
		light.getPosition(passthough);
		shader.setPosition(passthough);
		shader.setInnerConeAngle((float)Math.cos(Math.toRadians(light.getInnerCone())));
		shader.setOuterConeAngle((float)Math.cos(Math.toRadians(light.getOuterCone())));
		shader.setQuadraticAttenuation(light.getQuadraticAttenuation());
		shader.setVolumetricLightStrength(volumetricLightsStrength);


	}
}
