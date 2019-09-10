package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.lights.SpotLight;
import gloop.graphics.rendering.shading.posteffects.GBufferPostEffect;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

class SpotLightGBufferPostEffect extends GBufferPostEffect<SpotLightDeferredLightingPassShader> {
	private static final Vector3f PASSTHOUGH = new Vector3f();
	private static final Matrix4f PASSTHROUGH_VP_MATRIX = new Matrix4f();

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
		Matrix4f.mul(projectionmatrix, viewmatrix, PASSTHROUGH_VP_MATRIX);
		shader.setShadowCameraVPMatrix(PASSTHROUGH_VP_MATRIX);
		shader.setShadowMapZFar(light.renderCam.getzfar());
		TextureManager.bindAlbedoMap(light.getShadowMap());
		shader.setShadowMapTextureUnit(TextureUnit.ALBEDO_MAP);

		// light
		light.getColor(PASSTHOUGH);
		shader.setColor(PASSTHOUGH);
		light.getDirection(PASSTHOUGH);
		shader.setDirection(PASSTHOUGH);
		light.getPosition(PASSTHOUGH);
		shader.setPosition(PASSTHOUGH);
		shader.setInnerConeAngle((float)Math.cos(Math.toRadians(light.getInnerCone())));
		shader.setOuterConeAngle((float)Math.cos(Math.toRadians(light.getOuterCone())));
		shader.setQuadraticAttenuation(light.getQuadraticAttenuation());
		shader.setVolumetricLightStrength(volumetricLightsStrength);


	}
}
