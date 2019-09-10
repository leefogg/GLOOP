package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.materials.ShaderToyShader;

import java.io.IOException;

public class VignetteShader extends ShaderToyShader {
	private Uniform1f start, end;

	public VignetteShader() throws ShaderCompilationException, IOException {
		super("res/_SYSTEM/Shaders/PostEffects/Vignette/FragmentShader.frag");
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		start = new Uniform1f(this, "Start");
		end = new Uniform1f(this, "End");
	}

	public void setStart(float start) { this.start.set(start); }
	public void setEnd(float end) { this.end.set(end); }

	@Override
	protected void setDefaultCustomUniformValues() {
		super.setDefaultCustomUniformValues();

		start.set(0);
		end.set(0.75f);
	}
}
