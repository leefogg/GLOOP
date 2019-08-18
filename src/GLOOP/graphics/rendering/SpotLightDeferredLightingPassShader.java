package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

class SpotLightDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform3f
			position,
			direction,
			color;
	private Uniform1f
			innerCone,
			outerCone,
			quadraticAttenuation,
			volumetricLightStrength,
			zFar;
	private Uniform16f shadowVPMatrix;
	private Uniform1i shadowMap;

	SpotLightDeferredLightingPassShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
			"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/SpotLight.glsl"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		position = new Uniform3f(this, "position");
		color = new Uniform3f(this, "color");
		direction = new Uniform3f(this, "direction");
		innerCone = new Uniform1f(this, "innerCone");
		outerCone = new Uniform1f(this, "outerCone");
		quadraticAttenuation = new Uniform1f(this, "quadraticAttenuation");
		volumetricLightStrength = new Uniform1f(this, "VolumetricLightStrength");

		shadowMap = new Uniform1i(this, "shadowMap");
		shadowVPMatrix = new Uniform16f(this, "shadowmapVPMatrix");
		zFar = new Uniform1f(this, "zFar");
	}

	public void setPosition(Vector3f pos) { position.set(pos); }
	public void setDirection(Vector3f direction) { this.direction.set(direction); }
	public void setColor(Vector3f color) { this.color.set(color); }
	public void setInnerConeAngle(float angle) { innerCone.set(angle); }
	public void setOuterConeAngle(float angle) { outerCone.set(angle); }
	public void setQuadraticAttenuation(float attenuation) { quadraticAttenuation.set(attenuation); }
	public void setVolumetricLightStrength(float strength) { volumetricLightStrength.set(strength); }

	public void setShadowCameraVPMatrix(Matrix4f matrix) { shadowVPMatrix.set(matrix); }
	public void setShadowMapTextureUnit(int unit) { shadowMap.set(unit); }
	public void setShadowMapZFar(float zfar) { zFar.set(zfar); }
}
