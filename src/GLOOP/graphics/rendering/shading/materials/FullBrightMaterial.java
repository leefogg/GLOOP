package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;

import java.io.IOException;

public final class FullBrightMaterial extends Material<ShaderProgram> {
	private static FullBrightShader defaultshader;

	private Texture albedo = Texture.blank;
	private ShaderProgram shader;

	public FullBrightMaterial(Texture albedo) throws IOException {
		this(getDefultShaderSingleton());
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

	public static final FullBrightShader getDefultShaderSingleton() throws IOException {
		if (defaultshader == null)
			defaultshader = new FullBrightShader();

		return defaultshader;
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
	public boolean SupportsShadowMaps() { return true; }
}
