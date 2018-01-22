package engine.graphics.shading.materials;

import engine.graphics.shading.ShaderProgram;

public abstract class Material<T extends ShaderProgram> {
	public void bind() {
		getShader().bind();
	}

	public abstract T getShader();

	public abstract void commit();

	public boolean isTransparent() { return getShader().supportsTransparency() && hasTransparency(); }

	protected abstract boolean hasTransparency();
}
