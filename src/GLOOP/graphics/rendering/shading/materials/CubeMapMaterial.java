package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.texturing.CubeMap;
import GLOOP.graphics.rendering.texturing.TextureManager;

import java.io.IOException;

public final class CubeMapMaterial extends Material<CubeMapShader> {
	private static CubeMapShader shader;
	private final CubeMap cubeMap;

	public CubeMapMaterial(CubeMap texture) throws IOException {
		shader = getShaderSingleton();
		cubeMap = texture;
	}

	private static CubeMapShader getShaderSingleton() throws IOException {
		if (shader == null)
			return shader = new CubeMapShader();

		return shader;
	}

	@Override
	public CubeMapShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindReflectionMap(cubeMap);

		GLOOP.graphics.cameras.Camera camera = Renderer.getCurrentCamera();
		shader.setProjectionMatrix(camera.getProjectionMatrix());
		shader.setViewMatrix(camera.getViewMatrix());
	}

	@Override
	protected boolean hasTransparency() { return false; }

}
