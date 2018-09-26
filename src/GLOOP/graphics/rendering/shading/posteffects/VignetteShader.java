package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.materials.ShaderToyShader;

import java.io.IOException;

public class VignetteShader extends ShaderToyShader {
	private Uniform1f Start, End;

	public VignetteShader() throws ShaderCompilationException, IOException {
		super("res/_SYSTEM/Shaders/PostEffects/Vignette/FragmentShader.frag");
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
