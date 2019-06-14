package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.texturing.Texture;

import java.io.IOException;

public class SSAOGBufferPostEffect extends GBufferPostEffect {
	private static SSAOGBufferPostEffectShader shader;

	private int totalSamples = 16;
	private float
			intensity = 0.5f,
			bias = 0.05f,
			sampleRadius = 0.02f,
			maxDistance = 0.07f;

	public SSAOGBufferPostEffect(Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) throws IOException {
		super(getShaderSingleton(), normalbuffer, specularbuffer, positionbuffer);
	}
	public SSAOGBufferPostEffect() throws IOException {
		super(getShaderSingleton());
	}

	private static GBufferLightingShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new SSAOGBufferPostEffectShader();

		return shader;
	}

	public void setTotalSamples(int totalsamples) { totalSamples = totalsamples; }
	public void setIntensity(float intensity) { this.intensity = intensity; }
	public void setBias(float bias) { this.bias = bias; }
	public void setSampleRadius(float sampleradius) { sampleRadius = sampleradius; }
	public void setMaxDistanceDifference(float setmaxdistancedifference) { maxDistance = setmaxdistancedifference; }

	@Override
	public void commit() {
		super.commit();

		shader.setTotalSamples(totalSamples);
		shader.setIntensity(intensity);
		shader.setBias(bias);
		shader.setSampleRadius(sampleRadius);
		shader.setMaxDistanceDifference(maxDistance);
	}
}
