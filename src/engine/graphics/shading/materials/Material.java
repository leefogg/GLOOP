package engine.graphics.shading.materials;

import engine.graphics.cameras.Camera;
import engine.graphics.shading.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public abstract class Material<T extends ShaderProgram> {
	public void bind() {
		getShader().bind();
	}

	public abstract T getShader();

	public abstract void commit();

	public void setCameraAttributes(Camera currentcamera, Matrix4f modelmatrix) {
		getShader().setCameraUniforms(currentcamera, modelmatrix);
	}

	public boolean isTransparent() { return getShader().supportsTransparency() && hasTransparency(); }

	public boolean useDeferredPipeline() { return false; } // TODO: Maybe use a private boolean?

	protected abstract boolean hasTransparency();
}
