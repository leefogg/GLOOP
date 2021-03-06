package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

class AmbientLightDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform3f ambientColor;

	AmbientLightDeferredLightingPassShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/AmbientLight.glsl"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		ambientColor = new Uniform3f(this, "ambientColor");
	}

	public void setAmbientColor(Vector3f color) { ambientColor.set(color); }
}
