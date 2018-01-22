package engine.graphics.shading.posteffects;

import engine.graphics.shading.GLSL.Uniform1f;

import java.io.IOException;

class FXAAShader extends PostEffectShader {
	private Uniform1f span;
	public FXAAShader() throws IOException {
		super(
				"res/shaders/Post Effects/FXAA/VertexShader.vert",
				"res/shaders/Post Effects/FXAA/FragmentShader.frag"
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
