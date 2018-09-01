package engine.graphics.shading.materials;

import engine.graphics.rendering.Renderer;
import engine.graphics.textures.CubeMap;
import engine.graphics.textures.TextureManager;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class ChromeMaterial extends Material<ChromeShader> {
	private static ChromeShader Shader;
	private static Vector3f CameraPosition = new Vector3f();

	private CubeMap EnvironmentMap;

	public ChromeMaterial(CubeMap environmentMap) throws IOException {
		getShaderSingleton();

		setEnvironmentMap(environmentMap);
	}

	private static ChromeShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ChromeShader();

		return Shader;
	}

	public void setEnvironmentMap(CubeMap environmentMap) {
		EnvironmentMap = environmentMap;
	}

	@Override
	public ChromeShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Renderer.getCurrentCamera().getPosition(CameraPosition);
		Shader.setCameraPosition(CameraPosition);

		TextureManager.bindReflectionMap(EnvironmentMap);
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}
}
