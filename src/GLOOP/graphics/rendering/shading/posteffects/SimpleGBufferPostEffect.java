package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.texturing.Texture;

public class SimpleGBufferPostEffect<T extends GBufferLightingShader> extends GBufferPostEffect<T> {
	public SimpleGBufferPostEffect(T shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}
	public SimpleGBufferPostEffect(T shader) {
		super(shader);
	}
}
