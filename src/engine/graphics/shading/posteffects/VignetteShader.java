package engine.graphics.shading.posteffects;

import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.materials.ShaderToyShader;

import java.io.IOException;

public class VignetteShader extends ShaderToyShader {
	private Uniform1f Power;

	public VignetteShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Post Effects/shadertoy/vertexShader.vert",
				"res/shaders/Post Effects/Vignette/FragmentShader.frag"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		Power = new Uniform1f(this, "Power");
	}

	public void setStrength(float strength) { Power.set(strength); }
}
