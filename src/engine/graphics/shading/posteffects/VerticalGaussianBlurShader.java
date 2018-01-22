package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;

import java.io.IOException;

public final class VerticalGaussianBlurShader extends PostEffectShader {
	private Uniform1i
		texture,
		screenHeight;

	public VerticalGaussianBlurShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Post Effects/Blur/Vertical.vert",
				"res/shaders/Post Effects/Blur/Vertical.frag"
			);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new Uniform1i(this, "Texture");
		screenHeight = new Uniform1i(this, "screenHeight");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setScreenHeight(Viewport.getHeight());
	}

	public void setTexture(int unit) {
		texture.set(unit);
	}

	public void setScreenHeight(int height) {
		screenHeight.set(height);
	}
}
