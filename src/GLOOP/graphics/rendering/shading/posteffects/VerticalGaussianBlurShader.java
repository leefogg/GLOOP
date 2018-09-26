package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

public final class VerticalGaussianBlurShader extends PostEffectShader {
	private Uniform1i
		texture,
		screenHeight;

	public VerticalGaussianBlurShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/Blur/Vertical.vert",
				"res/_SYSTEM/Shaders/PostEffects/Blur/Vertical.frag"
			);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new CachedUniform1i(this, "Texture");
		screenHeight = new CachedUniform1i(this, "screenHeight");
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
