package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleMaterial extends Material<ParticleShader> {
	private static ParticleShader Shader;

	private final Texture texture;
	private Matrix4f projectionMatrix, viewMatrix;
	private float radius = 1;

	public ParticleMaterial(Texture texture) throws IOException {
		Shader = getShaderSingleton();
		this.texture = texture;
	}

	private static ParticleShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new ParticleShader();

		return Shader;
	}

	@Override
	public ParticleShader getShader() {	return Shader; }

	@Override
	public void commit() {
		Shader.setProjectionMatrix(projectionMatrix);
		Shader.setViewMatrix(viewMatrix);
		Shader.setRadius(radius);
	}

	public void setProjectionMatrix(Matrix4f projectionmatrix) { projectionMatrix = projectionmatrix; }

	public void setViewMatrix(Matrix4f viewmatrix) { viewMatrix = viewmatrix;}

	@Override
	protected boolean hasTransparency() {
		return texture.isTransparent() && Shader.supportsTransparency();
	}

	public void setRadius(float radius) { this.radius = radius; }

	@Override
	public boolean supportsShadowMaps() { return true; }

	@Override
	public Texture getAlbedoTexture() {
		return texture;
	}
}
