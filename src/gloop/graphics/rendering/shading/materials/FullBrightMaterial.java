package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;

import java.io.IOException;

public final class FullBrightMaterial extends Material<ShaderProgram> {
	private static FullBrightShader Defaultshader;

	private Texture albedo = Texture.Blank;
	private final ShaderProgram shader;

	public FullBrightMaterial(Texture albedo) throws IOException {
		this(getDefaultShaderSingleton());
		setAlbedoTexture(albedo);
	}
	public FullBrightMaterial(FullBrightShader shader) {
		this.shader = shader;
	}
	public FullBrightMaterial(GUIShader shader, Texture albedo) {
		this(shader);
		setAlbedoTexture(albedo);
	}
	public FullBrightMaterial(GUIShader shader) {
		this.shader = shader;
	}
	public FullBrightMaterial(CubeMapShader shader, Texture albedo) {
		this.shader = shader;
		setAlbedoTexture(albedo);
	}

	public static final FullBrightShader getDefaultShaderSingleton() throws IOException {
		if (Defaultshader == null)
			Defaultshader = new FullBrightShader();

		return Defaultshader;
	}

	public void setAlbedoTexture(Texture texture) {
		albedo = texture;
	}

	@Override
	public ShaderProgram getShader() {
		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(albedo);
	}

	@Override
	protected boolean hasTransparency() { return albedo != null && albedo.isTransparent(); }

	@Override
	public boolean supportsShadowMaps() { return true; }
}
