package engine.graphics.shading.posteffects;

import engine.graphics.shading.GLSL.Uniform1f;

import java.io.IOException;

class GammaCorrectionShader extends PostEffectShader {
	private Uniform1f gamma;

	public GammaCorrectionShader() throws IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/GammaCorrection/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/GammaCorrection/FragmentShader.frag"
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
