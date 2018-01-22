package engine.graphics.shading.posteffects;

import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.posteffects.PostEffectShader;

import java.io.IOException;

public final class ExtractBrightShader extends PostEffectShader {
	public ExtractBrightShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Post Effects/ExtractBright/VertexShader.vert",
				"res/shaders/Post Effects/ExtractBright/FragmentShader.frag"
			);
	}
}
