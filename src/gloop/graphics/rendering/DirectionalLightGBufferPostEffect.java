package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.lights.DirectionalLight;
import gloop.graphics.rendering.shading.posteffects.GBufferPostEffect;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class DirectionalLightGBufferPostEffect extends GBufferPostEffect<DirectionalLightLightingPassShader> {
	private static final Vector3f PASSTHOUGH = new Vector3f();
	private static final Matrix4f PASSTHROUGH_VP_MATRIX = new Matrix4f();

	private DirectionalLight light;

	public DirectionalLightGBufferPostEffect(DirectionalLightLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void set(DirectionalLight light) { this.light = light; }

	@Override
	public void commit() {
		super.commit();

		Matrix4f.mul(light.renderCam.getProjectionMatrix(), light.renderCam.getViewMatrix(), PASSTHROUGH_VP_MATRIX);
		shader.setShadowCameraVPMatrix(PASSTHROUGH_VP_MATRIX);
		light.renderCam.getPosition(PASSTHOUGH);
		shader.setShadowCameraPosition(PASSTHOUGH);
		shader.setShadowMapZFar(light.renderCam.getzfar());
		TextureManager.bindAlbedoMap(light.getShadowMap());
		shader.setShadowMapTextureUnit(TextureUnit.ALBEDO_MAP);

		light.getDiffuseColor(PASSTHOUGH);
		shader.setColor(PASSTHOUGH);
		light.getDirection(PASSTHOUGH);
		shader.setDirection(PASSTHOUGH);
	}
}
