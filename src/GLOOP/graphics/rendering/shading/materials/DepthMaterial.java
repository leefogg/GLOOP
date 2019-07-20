package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.cameras.Camera;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class DepthMaterial extends Material<DepthShader> {
	private DepthShader shader;

	public DepthMaterial() throws IOException {
		shader = getShaderSingleton();
	}

	private DepthShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new DepthShader();

		return shader;
	}

	@Override
	public DepthShader getShader() {
		return shader;
	}

	@Override
	public void commit() {

	}

	@Override
	public void setCameraAttributes(Camera currentcamera, Matrix4f modelmatrix) {
		super.setCameraAttributes(currentcamera, modelmatrix);
		shader.set(currentcamera);
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}
}
