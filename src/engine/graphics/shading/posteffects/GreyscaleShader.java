package engine.graphics.shading.posteffects;

import engine.graphics.shading.ShaderCompilationException;

import java.io.IOException;

public class GreyscaleShader extends PostEffectShader { // TODO: Make post effect
	public GreyscaleShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/Greyscale/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/Greyscale/FragmentShader.frag"
		);
	}
}
