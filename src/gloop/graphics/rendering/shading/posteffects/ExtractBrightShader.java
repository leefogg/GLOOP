package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

public final class ExtractBrightShader extends PostEffectShader {
	public ExtractBrightShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/ExtractBright/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/ExtractBright/FragmentShader.frag"
			);
	}
}
