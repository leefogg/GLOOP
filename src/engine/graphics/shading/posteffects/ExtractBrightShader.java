package engine.graphics.shading.posteffects;

import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.posteffects.PostEffectShader;

import java.io.IOException;

public final class ExtractBrightShader extends PostEffectShader {
	public ExtractBrightShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/ExtractBright/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/ExtractBright/FragmentShader.frag"
			);
	}
}
