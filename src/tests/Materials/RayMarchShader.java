package tests.Materials;

import gloop.graphics.cameras.PerspectiveCamera;
import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.GBufferShader;
import gloop.graphics.rendering.shading.glsl.Uniform16f;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform2f;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

// Basically a ShaderToyShader but with a 3D vertex shader
public class RayMarchShader extends GBufferShader {
	private Uniform16f viewMatrix;
	private Uniform3f cameraRotation;
	private Uniform1f cameraFOV;
	private Uniform2f resolution;

	public RayMarchShader() throws IOException {
		super(
			"res\\shaders\\Tests\\RayMarching\\VertexShader.vert",
			"res\\shaders\\Tests\\RayMarching\\raymarching.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		viewMatrix = new Uniform16f(this, "ViewMatrix");
		cameraRotation = new Uniform3f(this, "CameraRotation");
		cameraFOV = new Uniform1f(this, "CameraFOV");
		resolution = new Uniform2f(this, "Resolution");
	}

	public void setViewMatrix(Matrix4f viewmatrix) { viewMatrix.set(viewmatrix);}

	public void setCameraRotation(Vector3f rotation) { cameraRotation.set(rotation); }

	public void setCameraFOV(float fov) { cameraFOV.set(fov); }

	public void setResolution(int width, int height) {	resolution.set(width, height); }

	@Override
	protected void setDefaultCustomUniformValues() {
		super.setDefaultCustomUniformValues();

		cameraFOV.set(PerspectiveCamera.DEFAULT_FOV);
	}

	@Override
	public boolean supportsTransparency() { return false; }
}
