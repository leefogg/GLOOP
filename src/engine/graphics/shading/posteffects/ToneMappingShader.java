package engine.graphics.shading.posteffects;

import engine.graphics.shading.GLSL.Uniform1f;

import java.io.IOException;

public class ToneMappingShader extends PostEffectShader {
	private Uniform1f exposure;

	public ToneMappingShader() throws IOException {
		super(
				"res/shaders/post effects/ToneMap/VertexShader.vert",
				"res/shaders/post effects/ToneMap/FragmentShader.frag"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		exposure = new Uniform1f(this, "exposure");
	}

	public void setExposure(float exposure) {
		this.exposure.set(exposure);
	}
}
