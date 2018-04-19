package engine.graphics.shading.materials;

import engine.graphics.shading.ShaderCompilationException;

import java.io.IOException;

public final class GUIShader extends FullBrightShader {//TODO: Replace with Shader hardcoded GLSL in Java
	public GUIShader() throws ShaderCompilationException, IOException {
		super(
			"res/shaders/GUIShader/VertexShader.vert",
			"res/shaders/GUIShader/FragmentShader.frag"
		);
	}
}
