package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Vector3f;

public class AmbientLightGBufferPostEffect extends GBufferPostEffect<AmbientLightDeferredLightingPassShader> {
	private Vector3f ambientColor = new Vector3f();

	AmbientLightGBufferPostEffect(AmbientLightDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void setAmbientColor(Vector3f color) { ambientColor.set(color); }

	@Override
	public void commit() {
		super.commit();

		shader.setAmbientColor(ambientColor);
	}
}
