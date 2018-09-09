package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Viewport;
import engine.graphics.shading.GLSL.CachedUniform1i;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;

import java.io.IOException;

public final class HorizontalGaussianBlurShader extends PostEffectShader {
	private Uniform1i
		texture,
		screenWidth;

	public HorizontalGaussianBlurShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Post Effects/Blur/Horizontal.vert",
				"res/shaders/Post Effects/Blur/Horizontal.frag"
			);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new CachedUniform1i(this, "Texture");
		screenWidth = new CachedUniform1i(this, "screenWidth");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		// Don't know what texture to bind.
		setScreenWidth(Viewport.getWidth());
	}

	public void setTexture(int unit) {
		texture.set(unit);
	}

	public void setScreenWidth(int width) {
		screenWidth.set(width);
	}
}
