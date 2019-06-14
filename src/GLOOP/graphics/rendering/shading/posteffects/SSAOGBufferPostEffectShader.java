package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.general.math.MathFunctions;
import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class SSAOGBufferPostEffectShader extends GBufferLightingShader {
	private static Vector3f PassThrough = new Vector3f();
	private static Matrix4f RotationMatrix = new Matrix4f();

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

		camera.getRotation(PassThrough);
		MathFunctions.createRotatationMatrix(PassThrough, RotationMatrix);
		RotationMatrix.invert();
		rotationMatrix.set(RotationMatrix);
		time.set(Viewport.getElapsedSeconds());
	}

	public void setTotalSamples(int totalsamples) { samples.set(totalsamples); }
	public void setIntensity(float intensity) { this.intensity.set(intensity); }
	public void setBias(float bias) { this.bias.set(bias); }
	public void setSampleRadius(float sampleradius) { this.sampleRadius.set(sampleradius); }
	public void setMaxDistanceDifference(float maxsampledistance) { maxDistance.set(maxsampledistance); }
}
