package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;

public class DepthMaterial extends Material<DepthShader> {
	private DepthShader shader;
	private Texture AlbedoMap;

	public DepthMaterial(DepthShader depthShader) {
		shader = depthShader;
	}

	@Override
	public DepthShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(AlbedoMap);
	}

	public void setAlbedoMap(Texture albedoMap) { AlbedoMap = albedoMap; }

	@Override
	protected boolean hasTransparency() { return AlbedoMap.isTransparent(); }

	@Override
	public boolean SupportsShadowMaps() { return true; }

	@Override
	public Texture GetAlbedoTexture() { return AlbedoMap; }
}
