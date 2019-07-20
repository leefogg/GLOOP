package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.Viewport;

import java.io.IOException;

public class ShaderToyMaterial extends Material<ShaderToyShader> {
	private ShaderToyShader shader;

	public ShaderToyMaterial(String fragmentshaderpath) throws IOException {
		shader = new ShaderToyShader(fragmentshaderpath);
	}

	@Override
	public ShaderToyShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setResolution(Viewport.getWidth(), Viewport.getHeight());
		shader.setTime(Viewport.getElapsedSeconds());
		//TODO: Update mouse corordinates
	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean SupportsShadowMaps() { return false; }
}
