package tests.Materials;

import GLOOP.graphics.cameras.PerspectiveCamera;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.shading.materials.Material;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class RayMarchMaterial extends Material<RayMarchShader> {
	private static RayMarchShader shader;
	private static Vector3f temp = new Vector3f();

	public RayMarchMaterial() throws IOException {
		this.shader = getShaderSingleton();
	}

	private static final RayMarchShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new RayMarchShader();

		return shader;
	}

	@Override
	public RayMarchShader getShader() {
		return shader;
	}

	Matrix4f vpmatrix = new Matrix4f();
	@Override
	public void commit() {
		shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
		shader.setTime(Viewport.getElapsedSeconds());

		PerspectiveCamera currentCamera = (PerspectiveCamera)Renderer.getCurrentCamera();
		Matrix4f.mul(currentCamera.getProjectionMatrix(), currentCamera.getViewMatrix(), vpmatrix);
		shader.setViewMatrix(vpmatrix);
		currentCamera.getPosition(temp);
		shader.setCameraPosition(temp);
		currentCamera.getRotation(temp);
		shader.setCameraRotation(temp);
		shader.setCameraFOV(currentCamera.getFov());
		shader.setzfar(currentCamera.getzfar());
		//TODO: Update mouse corordinates
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean usesDeferredPipeline() { return true; }
}
