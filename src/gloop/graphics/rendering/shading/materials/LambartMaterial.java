package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class LambartMaterial extends Material<LambartShader> {
	private static final Vector3f PASSTHROUGH = new Vector3f();
	private static LambartShader Shader;

	private Texture albedo = Texture.Blank;


	public LambartMaterial(Texture albedomap) throws IOException {
		this();
		setAlbedoTexture(albedomap);
	}
	private LambartMaterial() throws IOException {
		Shader = getShaderSingleton();
	}

	private static LambartShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new LambartShader();

		return Shader;
	}

	public void setAlbedoTexture(Texture texture) {
		albedo = texture;
	}

	@Override
	public LambartShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(albedo);

		PointLight light1 = Renderer.getRenderer().getScene().getPointLight(0);
		light1.getPosition(PASSTHROUGH);
		Shader.setLightPosition(PASSTHROUGH);
		Vector3f lightcolor = light1.getColor(PASSTHROUGH);
		Shader.setLightColor(lightcolor.x, lightcolor.y, lightcolor.z);
		Shader.setLightquadraticAttenuation(light1.quadraticAttenuation);
	}

	@Override
	protected boolean hasTransparency() { return albedo.isTransparent(); }

	@Override
	public boolean supportsShadowMaps() { return true; }

	@Override
	public Texture getAlbedoTexture() { return albedo; }
}
