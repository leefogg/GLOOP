package engine.graphics.shading.materials;

import engine.graphics.rendering.Renderer;
import engine.graphics.textures.CubeMap;
import engine.graphics.textures.TextureManager;

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

		engine.graphics.cameras.Camera camera = Renderer.getForwardRenderer().getScene().currentCamera;
		shader.setProjectionMatrix(camera.getProjectionMatrix());
		shader.setViewMatrix(camera.getViewMatrix());
	}

	@Override
	protected boolean hasTransparency() { return false; }

}
