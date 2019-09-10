package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.texturing.Texture;

public class SimpleGBufferPostEffect<T extends GBufferLightingShader> extends GBufferPostEffect<T> {
	public SimpleGBufferPostEffect(T shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		super(shader, normalbuffer, specularbuffer, positionbuffer);
	}
	public SimpleGBufferPostEffect(T shader) {
		super(shader);
	}
}
