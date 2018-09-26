package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

public final class GUIShader extends FullBrightShader {//TODO: Replace with Shader hardcoded GLSL in Java
	public GUIShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/GUI/VertexShader.vert",
			"res/_SYSTEM/Shaders/GUI/FragmentShader.frag"
		);
	}
}
