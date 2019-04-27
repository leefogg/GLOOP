package tests.Materials;

import GLOOP.graphics.cameras.PerspectiveCamera;
import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.GBufferShader;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform2f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.shading.materials.ShaderToyShader;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

// Basically a ShaderToyShader but with a 3D vertex shader
public class RayMarchShader extends GBufferShader {
	private Uniform16f viewMatrix;
	private Uniform3f CameraRotation;
	private Uniform1f CameraFOV;
	private Uniform2f Resolution;

	public RayMarchShader() throws IOException {
		super(
				"res\\shaders\\Tests\\RayMarching\\VertexShader.vert",
				"res\\shaders\\Tests\\RayMarching\\raymarching.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		viewMatrix = new Uniform16f(this, "ViewMatrix");
		CameraRotation = new Uniform3f(this, "CameraRotation");
		CameraFOV = new Uniform1f(this, "CameraFOV");
		Resolution = new Uniform2f(this, "Resolution");
	}

	public void setViewMatrix(Matrix4f viewmatrix) { viewMatrix.set(viewmatrix);}

	public void setCameraRotation(Vector3f rotation) { CameraRotation.set(rotation); }

	public void setCameraFOV(float fov) { CameraFOV.set(fov); }

	public void setResolution(int width, int height) {	Resolution.set(width, height); }

	@Override
	protected void setDefaultCustomUniformValues() {
		super.setDefaultCustomUniformValues();

		CameraFOV.set(PerspectiveCamera.DEFAULT_FOV);
	}

	@Override
	public boolean supportsTransparency() { return false; }
}
