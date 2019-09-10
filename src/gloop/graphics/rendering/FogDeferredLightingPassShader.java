package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

class FogDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform1f fogFactor;
	private Uniform3f fogColor;

	FogDeferredLightingPassShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/Fog.glsl"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		fogFactor = new Uniform1f(this, "fogDensity");
		fogColor = new Uniform3f(this, "fogColor");
	}

	public void setFogFactor(float factor) { fogFactor.set(factor); }
	public void setFogColor(Vector3f color) { fogColor.set(color); }
}
