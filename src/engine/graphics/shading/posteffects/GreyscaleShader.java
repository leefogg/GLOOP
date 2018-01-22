package engine.graphics.shading.posteffects;

import engine.graphics.shading.ShaderCompilationException;

import java.io.IOException;

public class GreyscaleShader extends PostEffectShader { // TODO: Make post effect
	public GreyscaleShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Post Effects/Greyscale/VertexShader.vert",
				"res/shaders/Post Effects/Greyscale/FragmentShader.frag"
		);
	}
}
