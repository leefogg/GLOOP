package engine.graphics.shading.materials;

import engine.graphics.cameras.Camera;
import engine.graphics.textures.Texture;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleMaterial extends Material<ParticleShader> {
	private static ParticleShader shader;

	private Texture texture;

	public ParticleMaterial(Texture texture) throws IOException {
		shader = getShaderSingleton();
		this.texture = texture;
	}

	private static ParticleShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new ParticleShader();

		return shader;
	}

	@Override
	public ParticleShader getShader() {	return shader; }

	@Override
	public void commit() {

	}

	@Override
	public void setCameraAttributes(Camera currentcamera, Matrix4f modelmatrix) {
		shader.setProjectionMatrix(currentcamera.getProjectionMatrix());
		shader.setViewMatrix(currentcamera.getViewMatrix());
		shader.setModelMatrix(modelmatrix);
	}

	@Override
	protected boolean hasTransparency() {
		return texture.isTransparent() && shader.supportsTransparency();
	}
}
