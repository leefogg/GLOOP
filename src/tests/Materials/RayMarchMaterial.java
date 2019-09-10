package tests.Materials;

import gloop.graphics.cameras.PerspectiveCamera;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.shading.materials.Material;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class RayMarchMaterial extends Material<RayMarchShader> {
	private static RayMarchShader Shader;
	private static final Vector3f TEMP = new Vector3f();

	public RayMarchMaterial() throws IOException {
		Shader = getShaderSingleton();
	}

	private static RayMarchShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new RayMarchShader();

		return Shader;
	}

	@Override
	public RayMarchShader getShader() {
		return Shader;
	}

	Matrix4f vpmatrix = new Matrix4f();
	@Override
	public void commit() {
		Shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
		Shader.setTime(Viewport.getElapsedSeconds());

		PerspectiveCamera currentCamera = (PerspectiveCamera)Renderer.getCurrentCamera();
		Matrix4f.mul(currentCamera.getProjectionMatrix(), currentCamera.getViewMatrix(), vpmatrix);
		Shader.setViewMatrix(vpmatrix);
		currentCamera.getPosition(TEMP);
		Shader.setCameraPosition(TEMP);
		currentCamera.getRotation(TEMP);
		Shader.setCameraRotation(TEMP);
		Shader.setCameraFOV(currentCamera.getFov());
		Shader.setzfar(currentCamera.getzfar());
		//TODO: Update mouse corordinates
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean usesDeferredPipeline() { return true; }

	@Override
	public boolean supportsShadowMaps() { return true; }
}
