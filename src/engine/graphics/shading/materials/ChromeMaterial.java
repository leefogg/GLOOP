package engine.graphics.shading.materials;

import engine.graphics.rendering.Renderer;
import engine.graphics.textures.CubeMap;
import engine.graphics.textures.TextureManager;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class ChromeMaterial extends Material<ChromeShader> {
	private static ChromeShader Shader;
	private static Vector3f Temp = new Vector3f();

	private CubeMap CubeMap;

	public ChromeMaterial(CubeMap cubemap) throws IOException {
		getShaderSingleton();

		setEnvironmentMap(cubemap);
	}

	private static ChromeShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ChromeShader();

		return Shader;
	}

	public void setEnvironmentMap(CubeMap probe) { CubeMap = probe; }

	@Override
	public ChromeShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Renderer.getCurrentCamera().getPosition(Temp);
		Shader.setCameraPosition(Temp);
		CubeMap.getPosition(Temp);
		Shader.setEnvironmentMapPosition(Temp);
		CubeMap.getSize(Temp);
		Shader.setEnvironmentMapSize(Temp);

		TextureManager.bindReflectionMap(CubeMap);
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}
}
