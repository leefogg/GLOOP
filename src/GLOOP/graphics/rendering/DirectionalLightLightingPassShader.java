package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class DirectionalLightLightingPassShader extends GBufferLightingShader {
	private Uniform3f direction, color;

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
	}

	public void setDirection(Vector3f direction) { this.direction.set(direction); }
	public void setColor(Vector3f color) { this.color.set(color); }
}
