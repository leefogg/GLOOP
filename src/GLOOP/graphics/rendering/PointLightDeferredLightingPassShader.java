package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

class PointLightDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform3f position, color;
	private Uniform1f quadraticAttenuation;
	private Uniform1i depthMap;

	PointLightDeferredLightingPassShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/PointLight.glsl"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		position = new Uniform3f(this, "position");
		color = new Uniform3f(this, "color");
		quadraticAttenuation = new Uniform1f(this, "quadraticAttenuation");
		depthMap = new Uniform1i(this, "depthMap");
	}

	public void setPosition(Vector3f pos) { position.set(pos); }
	public void setColor(Vector3f color) { this.color.set(color); }
	public void setQuadraticAttenuation(float attenuation) { quadraticAttenuation.set(attenuation); }
	public void setDepthMap(int unit) { this.depthMap.set(unit); }
}
