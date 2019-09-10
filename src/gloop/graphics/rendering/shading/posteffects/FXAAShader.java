package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.glsl.Uniform1f;

import java.io.IOException;

class FXAAShader extends PostEffectShader {
	private Uniform1f span;
	public FXAAShader() throws IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/FXAA/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/FXAA/FragmentShader.frag"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		span = new Uniform1f(this, "Span");
	}

	public void setSpan(float span) { this.span.set(span); }

	@Override
	protected void setDefaultCustomUniformValues() {
		super.setDefaultCustomUniformValues();

		span.set(8);
	}
}
