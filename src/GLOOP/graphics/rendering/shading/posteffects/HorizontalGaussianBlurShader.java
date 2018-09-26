package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

public final class HorizontalGaussianBlurShader extends PostEffectShader {
	private Uniform1i
		texture,
		screenWidth;

	public HorizontalGaussianBlurShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/Blur/Horizontal.vert",
				"res/_SYSTEM/Shaders/PostEffects/Blur/Horizontal.frag"
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
