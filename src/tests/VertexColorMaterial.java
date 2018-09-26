package tests;

import GLOOP.graphics.rendering.shading.materials.Material;

public class VertexColorMaterial extends Material<VertexColorShader> {
	public VertexColorShader shader;

	public VertexColorMaterial(VertexColorShader shader) {
		this.shader = shader;
	}

	@Override
	public VertexColorShader getShader() {
		return shader;
	}

	@Override
	public void commit() {

	}

	@Override
	protected boolean hasTransparency() {
		return false;
	}
}
