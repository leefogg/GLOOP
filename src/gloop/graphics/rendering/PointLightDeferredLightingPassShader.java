package gloop.graphics.rendering;

import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

class PointLightDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform3f position, color;
	private Uniform1f quadraticAttenuation;
	private Uniform1i depthMap;
	private Uniform1f zFar;

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
		zFar = new Uniform1f(this, "zFar");
	}

	public void setPosition(Vector3f pos) { position.set(pos); }
	public void setColor(Vector3f color) { this.color.set(color); }
	public void setQuadraticAttenuation(float attenuation) { quadraticAttenuation.set(attenuation); }
	public void setDepthMap(int unit) { this.depthMap.set(unit); }
	public void setzFar(float zfar) { zFar.set(zfar); }
}
