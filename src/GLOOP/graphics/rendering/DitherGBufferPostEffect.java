package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.posteffects.GBufferPostEffect;
import GLOOP.graphics.rendering.texturing.Texture;

class DitherGBufferPostEffect extends GBufferPostEffect<DitherDeferredLightingPassShader> {
	DitherGBufferPostEffect(DitherDeferredLightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}

	@Override
	public void commit() {
		super.commit();

		shader.setTime(Viewport.getElapsedSeconds());
	}
}
