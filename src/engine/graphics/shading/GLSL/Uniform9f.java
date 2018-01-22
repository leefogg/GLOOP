package engine.graphics.shading.GLSL;

import engine.graphics.shading.ShaderProgram;
import org.lwjgl.util.vector.Matrix3f;

public final class Uniform9f extends Uniform<Matrix3f> {

	public Uniform9f(ShaderProgram program, String name) {
		super(program, name);
	}

	@Override
	public void set(Matrix3f matrix) {
		load(location, matrix);
	}
}
