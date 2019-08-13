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

public class DirectionalLightLightingPassShader extends GBufferLightingShader {
	private Uniform3f direction, color, shadowCamPos;
	private Uniform16f shadowVPMatrix;
	private Uniform1i shadowMap;
	private Uniform1f zfar;

	DirectionalLightLightingPassShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/DirectionalLight.glsl"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		direction = new Uniform3f(this, "direction");
		color = new Uniform3f(this, "diffuseColor");

		shadowMap = new Uniform1i(this, "shadowMap");
		shadowCamPos = new Uniform3f(this, "shadowCameraPos");
		shadowVPMatrix = new Uniform16f(this, "shadowmapVPMatrix");
		zfar = new Uniform1f(this, "zFar");
	}

	public void setDirection(Vector3f direction) { this.direction.set(direction); }
	public void setColor(Vector3f color) { this.color.set(color); }

	public void setShadowCameraPosition(Vector3f campos) { shadowCamPos.set(campos); }
	public void setShadowCameraVPMatrix(Matrix4f matrix) { shadowVPMatrix.set(matrix); }
	public void setShadowMapTextureUnit(int unit) { shadowMap.set(unit); }
	public void setShadowMapZFar(float zfar) { this.zfar.set(zfar); }
}
