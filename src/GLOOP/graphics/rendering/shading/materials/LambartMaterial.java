package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class LambartMaterial extends Material<LambartShader> {
	private static LambartShader shader;

	private Texture albedo = Texture.blank;

	public static final Vector3f passthrough = new Vector3f();

	public LambartMaterial(Texture albedomap) throws IOException {
		this();
		setAlbedoTexture(albedomap);
	}
	private LambartMaterial() throws IOException {
		this.shader = getShaderSingleton();
	}

	private static final LambartShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new LambartShader();

		return shader;
	}

	public void setAlbedoTexture(Texture texture) {
		albedo = texture;
	}

	@Override
	public LambartShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(albedo);

		PointLight light1 = Renderer.getRenderer().getScene().getPointLight(0);
		light1.getPosition(passthrough);
		shader.setLightPosition(passthrough);
		Vector3f lightcolor = light1.getColor(passthrough);
		shader.setLightColor(lightcolor.x, lightcolor.y, lightcolor.z);
		shader.setLightquadraticAttenuation(light1.quadraticAttenuation);
	}

	@Override
	protected boolean hasTransparency() { return albedo.isTransparent(); }

}
