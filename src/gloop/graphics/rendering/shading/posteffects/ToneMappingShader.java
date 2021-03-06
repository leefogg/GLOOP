package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.glsl.Uniform1f;

import java.io.IOException;

public class ToneMappingShader extends PostEffectShader {
	private Uniform1f exposure;

	public ToneMappingShader() throws IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/ToneMap/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/ToneMap/FragmentShader.frag"
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
