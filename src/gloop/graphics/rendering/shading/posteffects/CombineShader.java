package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

final class CombineShader extends PostEffectShader {
	private Uniform1i texture1, texture2;

	public CombineShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/Combine/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/Combine/FragmentShader.frag"
			);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture1 = new CachedUniform1i(this, "Texture1");
		texture2 = new CachedUniform1i(this, "Texture2");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setTexture1(5);
		setTexture2(6);
	}

	public void setTexture1(int unit) {	texture1.set(unit); }

	public void setTexture2(int unit) {
		texture2.set(unit);
	}

}
