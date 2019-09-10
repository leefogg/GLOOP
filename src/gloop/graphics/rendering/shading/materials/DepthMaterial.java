package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;

public class DepthMaterial extends Material<DepthShader> {
	private final DepthShader shader;
	private Texture albedoMap;

	public DepthMaterial(DepthShader depthShader) {
		shader = depthShader;
	}

	@Override
	public DepthShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(albedoMap);
	}

	public void setAlbedoMap(Texture albedoMap) { this.albedoMap = albedoMap; }

	@Override
	protected boolean hasTransparency() { return albedoMap.isTransparent(); }

	@Override
	public boolean supportsShadowMaps() { return true; }

	@Override
	public Texture getAlbedoTexture() { return albedoMap; }
}
