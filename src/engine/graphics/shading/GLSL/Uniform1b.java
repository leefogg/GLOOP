package engine.graphics.shading.GLSL;

import engine.graphics.shading.ShaderProgram;

public final class Uniform1b extends Uniform<Boolean> {

	public Uniform1b(ShaderProgram program, String name) {
		super(program, name);
	}

	@Override
	public void set(Boolean value) {
		load(location, value);
	}
	public void set(boolean value) {
		load(location, value);
	}
}
