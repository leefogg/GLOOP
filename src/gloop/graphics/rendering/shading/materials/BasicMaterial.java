package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.shading.ShaderProgram;

public class BasicMaterial<T extends ShaderProgram> extends Material<T> {
	private final T shader;

	public BasicMaterial(T shader) {
		this.shader = shader;
	}

	@Override
	public T getShader() {
		return shader;
	}

	@Override
	public void commit() {
		// BasicShader's purpose, no uniforms to update
	}

	@Override
	protected boolean hasTransparency() {
		return shader.supportsTransparency();
	}

	@Override
	public boolean supportsShadowMaps() { return true; }
}
