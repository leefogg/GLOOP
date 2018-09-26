package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleMaterial extends Material<ParticleShader> {
	private static ParticleShader shader;

	private Texture texture;
	private Matrix4f ProjectionMatrix, ViewMatrix;
	private static float Radius = 1;

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
		shader.setProjectionMatrix(ProjectionMatrix);
		shader.setViewMatrix(ViewMatrix);
		shader.setRadius(Radius);
	}

	public void setProjectionMatrix(Matrix4f projectionmatrix) { ProjectionMatrix = projectionmatrix; }

	public void setViewMatrix(Matrix4f viewmatrix) { ViewMatrix = viewmatrix;}

	@Override
	protected boolean hasTransparency() {
		return texture.isTransparent() && shader.supportsTransparency();
	}

	public static void setRadius(float radius) { Radius = radius; }
}
