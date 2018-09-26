package GLOOP.graphics.rendering.shading.GLSL;

import GLOOP.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public final class Uniform16f extends Uniform<Matrix4f> {

	public Uniform16f(ShaderProgram program, String name) {
		super(program, name);
	}

	@Override
	public void set(Matrix4f matrix) {
		load(location, matrix);
	}
}
