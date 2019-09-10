package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

class DitherDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform1f time;

	DitherDeferredLightingPassShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/Dither.glsl"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		time = new Uniform1f(this, "time");
	}

	public void setTime(float seconds) { this.time.set(seconds); }
}
