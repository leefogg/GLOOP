package gloop.graphics.rendering.shading.materials;

import gloop.graphics.rendering.shading.ShaderCompilationException;

import java.io.IOException;

public final class GUIShader extends FullBrightShader {//TODO: Replace with Shader hardcoded glsl in Java
	public GUIShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/GUI/VertexShader.vert",
			"res/_SYSTEM/Shaders/GUI/FragmentShader.frag"
		);
	}
}
