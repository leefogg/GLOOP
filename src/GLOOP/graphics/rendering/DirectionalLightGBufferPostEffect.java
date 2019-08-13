package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.lights.DirectionalLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class DirectionalLightGBufferPostEffect extends GBufferPostEffect<DirectionalLightLightingPassShader> {
	private static Vector3f passthough = new Vector3f();
	private static Matrix4f passthroughVPMatrix = new Matrix4f();

	private DirectionalLight light;

	public DirectionalLightGBufferPostEffect(DirectionalLightLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void set(DirectionalLight light) { this.light = light; }

	@Override
	public void commit() {
		super.commit();

		Matrix4f.mul(light.renderCam.getProjectionMatrix(), light.renderCam.getViewMatrix(), passthroughVPMatrix);
		shader.setShadowCameraVPMatrix(passthroughVPMatrix);
		light.renderCam.getPosition(passthough);
		shader.setShadowCameraPosition(passthough);
		shader.setShadowMapZFar(light.renderCam.getzfar());
		TextureManager.bindAlbedoMap(light.getShadowMap());
		shader.setShadowMapTextureUnit(TextureUnit.AlbedoMap);

		light.getDiffuseColor(passthough);
		shader.setColor(passthough);
		light.getDirection(passthough);
		shader.setDirection(passthough);
	}
}
