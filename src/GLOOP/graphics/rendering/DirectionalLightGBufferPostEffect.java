package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.lights.DirectionalLight;
import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Vector3f;

public class DirectionalLightGBufferPostEffect extends GBufferPostEffect<DirectionalLightLightingPassShader> {
	private static Vector3f passthough = new Vector3f();

	private DirectionalLight light;

	public DirectionalLightGBufferPostEffect(DirectionalLightLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void set(DirectionalLight light) { this.light = light; }

	@Override
	public void commit() {
		super.commit();

		light.getDiffuseColor(passthough);
		shader.setColor(passthough);
		light.getDirection(passthough);
		shader.setDirection(passthough);
	}
}
