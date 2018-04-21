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
		shader.setModelMatrix(modelmatrix);

		Matrix4f modelviewmatrix = Matrix4f.mul(currentcamera.getViewMatrix(), modelmatrix, modelmatrix);
		/* Set top 3x3 matrix to
		[1,0,0]
		[0,1,0]
		[0,0,1]
		to remove rotation*/
		modelviewmatrix.m00 = 1;
		modelviewmatrix.m01 = 0;
		modelviewmatrix.m02 = 0;
		modelviewmatrix.m10 = 0;
		modelviewmatrix.m11 = 1;
		modelviewmatrix.m12 = 0;
		modelviewmatrix.m20 = 0;
		modelviewmatrix.m21 = 0;
		modelviewmatrix.m22 = 1;
		shader.setModelViewMatrix(modelviewmatrix);
	}

	@Override
	protected boolean hasTransparency() {
		return texture.isTransparent() && shader.supportsTransparency();
	}
}
