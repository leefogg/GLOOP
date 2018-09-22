package engine.graphics.shading.materials;

import engine.graphics.shading.ShaderProgram;

public class BasicMaterial<T extends ShaderProgram> extends Material<T> {
	private T shader;

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
}
