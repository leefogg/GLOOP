package engine.graphics.shading.materials;

import engine.graphics.textures.Texture;

import java.io.IOException;

public class ParticleMaterial extends Material<ParticleShader> {
	private static ParticleShader shader;

	private Texture texture;

	public ParticleMaterial(Texture texture) throws IOException {
		shader = getShaderSingleton();
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
	protected boolean hasTransparency() {
		return false;
	}
}
