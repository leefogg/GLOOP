package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.GLSL.Uniform2f;

import java.io.IOException;

class ChromaticAberrationShader extends PostEffectShader {
	private Uniform1f offset, rotation;
	private Uniform2f resolution;

	public ChromaticAberrationShader() throws IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/ChromaticAberration/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/ChromaticAberration/FragmentShader.frag"
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

		offset = new Uniform1f(this, "Offset");
		rotation = new Uniform1f(this, "Rotation");
		resolution = new Uniform2f(this, "Resolution");
	}

	public void setOffset(float offset) { this.offset.set(offset); }

	public void setRotation(float rotation) { this.rotation.set(rotation); }

	public void setResolution(float x, float y) { this.resolution.set(x, y); }

	@Override
	protected void setDefaultCustomUniformValues() {
		super.setDefaultCustomUniformValues();

		this.setResolution(Viewport.getWidth(), Viewport.getHeight());
	}
}
