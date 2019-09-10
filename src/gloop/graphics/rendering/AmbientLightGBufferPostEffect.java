package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.posteffects.GBufferPostEffect;
import gloop.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

public class AmbientLightGBufferPostEffect extends GBufferPostEffect<AmbientLightDeferredLightingPassShader> {
	private final Vector3f ambientColor = new Vector3f();

	AmbientLightGBufferPostEffect(AmbientLightDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	public void setAmbientColor(ReadableVector3f color) { ambientColor.set(color); }

	@Override
	public void commit() {
		super.commit();

		shader.setAmbientColor(ambientColor);
	}
}
