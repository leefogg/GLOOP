package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.Settings;
import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.texturing.Texture;

import java.io.IOException;

public class SSAOGBufferPostEffect extends GBufferPostEffect {
	private static SSAOGBufferPostEffectShader Shader;

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
		if (Shader == null)
			Shader = new SSAOGBufferPostEffectShader();

		return Shader;
	}

	public void setTotalSamples(int totalsamples) { totalSamples = totalsamples; }
	public void setIntensity(float intensity) { this.intensity = intensity; }
	public void setBias(float bias) { this.bias = bias; }
	public void setSampleRadius(float sampleradius) { sampleRadius = sampleradius; }
	public void setMaxDistanceDifference(float setmaxdistancedifference) { maxDistance = setmaxdistancedifference; }

	@Override
	public void commit() {
		super.commit();

		Shader.setTotalSamples(totalSamples);
		Shader.setIntensity(intensity);
		Shader.setBias(bias);
		Shader.setSampleRadius(sampleRadius);
		Shader.setMaxDistanceDifference(maxDistance);
		Shader.setElapsedSeconds(Settings.EnableDither ? Viewport.getElapsedSeconds() : 1);
	}
}
