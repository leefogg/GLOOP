package engine.graphics.shading.posteffects;

import engine.graphics.shading.GLSL.Uniform1f;

import java.io.IOException;

class GammaCorrectionShader extends PostEffectShader {
	private Uniform1f gamma;

	public GammaCorrectionShader() throws IOException {
		super(
				"res/shaders/post effects/GammaCorrection/VertexShader.vert",
				"res/shaders/post effects/GammaCorrection/FragmentShader.frag"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		gamma = new Uniform1f(this, "gamma");
	}

	public void setGamma(float gamma) {
		this.gamma.set(gamma);
	}
}
