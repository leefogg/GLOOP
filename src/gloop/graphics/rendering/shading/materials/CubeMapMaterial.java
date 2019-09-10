package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.texturing.CubeMap;
import gloop.graphics.rendering.texturing.TextureManager;

import java.io.IOException;

public final class CubeMapMaterial extends Material<CubeMapShader> {
	private static CubeMapShader Shader;
	private final CubeMap cubeMap;

	public CubeMapMaterial(CubeMap texture) throws IOException {
		Shader = getShaderSingleton();
		cubeMap = texture;
	}

	private static CubeMapShader getShaderSingleton() throws IOException {
		if (Shader == null)
			return Shader = new CubeMapShader();

		return Shader;
	}

	@Override
	public CubeMapShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		TextureManager.bindReflectionMap(cubeMap);

		gloop.graphics.cameras.Camera camera = Renderer.getCurrentCamera();
		Shader.setProjectionMatrix(camera.getProjectionMatrix());
		Shader.setViewMatrix(camera.getViewMatrix());
	}

	@Override
	protected boolean hasTransparency() { return false; }

	@Override
	public boolean supportsShadowMaps() { return false; }

}
