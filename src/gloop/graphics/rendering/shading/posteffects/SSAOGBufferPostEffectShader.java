package gloop.graphics.rendering.shading.posteffects;

import gloop.general.math.MathFunctions;
import gloop.graphics.cameras.Camera;
import gloop.graphics.rendering.shading.GBufferLightingShader;
import gloop.graphics.rendering.shading.glsl.Uniform16f;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class SSAOGBufferPostEffectShader extends GBufferLightingShader {
	private static final Vector3f PASS_THROUGH = new Vector3f();
	private static final Matrix4f ROTATION_MATRIX = new Matrix4f();

	private Uniform16f rotationMatrix;
	private Uniform1f time, intensity, bias, sampleRadius, maxDistance;
	private Uniform1i samples;

	public SSAOGBufferPostEffectShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/SSAO/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/SSAO/FragmentShader.frag"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		rotationMatrix = new Uniform16f(this, "RotationMatrix");
		time = new Uniform1f(this, "Time");
		samples = new Uniform1i(this, "Samples");
		intensity = new Uniform1f(this, "Intensity");
		bias = new Uniform1f(this, "Bias");
		sampleRadius = new Uniform1f(this, "SampleRadius");
		maxDistance = new Uniform1f(this, "maxDistance");
	}

	@Override
	public void setCameraAttributes(Camera camera) {
		super.setCameraAttributes(camera);

		camera.getRotation(PASS_THROUGH);
		MathFunctions.createRotatationMatrix(PASS_THROUGH, ROTATION_MATRIX);
		ROTATION_MATRIX.invert();
		rotationMatrix.set(ROTATION_MATRIX);
	}

	public void setTotalSamples(int totalsamples) { samples.set(totalsamples); }
	public void setIntensity(float intensity) { this.intensity.set(intensity); }
	public void setBias(float bias) { this.bias.set(bias); }
	public void setSampleRadius(float sampleradius) { this.sampleRadius.set(sampleradius); }
	public void setMaxDistanceDifference(float maxsampledistance) { maxDistance.set(maxsampledistance); }
	public void setElapsedSeconds(float seconds) { time.set(seconds); }
}
