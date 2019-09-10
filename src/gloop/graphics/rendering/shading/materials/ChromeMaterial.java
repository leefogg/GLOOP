package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.texturing.CubeMap;
import gloop.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class ChromeMaterial extends Material<ChromeShader> {
	private static ChromeShader Shader;
	private static final Vector3f TEMP = new Vector3f();

	private CubeMap cubeMap;

	public ChromeMaterial(CubeMap cubemap) throws IOException {
		getShaderSingleton();

		setEnvironmentMap(cubemap);
	}

	private static ChromeShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ChromeShader();

		return Shader;
	}

	public void setEnvironmentMap(CubeMap probe) { cubeMap = probe; }

	@Override
	public ChromeShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Renderer.getCurrentCamera().getPosition(TEMP);
		Shader.setCameraPosition(TEMP);
		cubeMap.getPosition(TEMP);
		Shader.setEnvironmentMapPosition(TEMP);
		cubeMap.getSize(TEMP);
		Shader.setEnvironmentMapSize(TEMP);

		TextureManager.bindReflectionMap(cubeMap);
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean supportsShadowMaps() { return true; }
}
