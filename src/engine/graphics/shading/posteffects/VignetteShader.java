package engine.graphics.shading.posteffects;

import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.materials.ShaderToyShader;

import java.io.IOException;

public class VignetteShader extends ShaderToyShader {
	private Uniform1f Start, End;

	public VignetteShader() throws ShaderCompilationException, IOException {
		super("res/shaders/Post Effects/Vignette/FragmentShader.frag");
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		Start = new Uniform1f(this, "Start");
		End = new Uniform1f(this, "End");
	}

	public void setStart(float start) { Start.set(start); }
	public void setEnd(float end) { End.set(end); }

	@Override
	protected void setDefaultCustomUniformValues() {
		super.setDefaultCustomUniformValues();

		Start.set(0);
		End.set(0.75f);
	}
}
