package engine.graphics.shading.GLSL;

import engine.graphics.shading.ShaderProgram;

public final class Uniform1i extends Uniform<Integer> {

	public Uniform1i(ShaderProgram program, String name) {
		super(program, name);
	}

	@Override
	public void set(Integer value) {
		load(location, value);
	}
	public void set(int value) {
		load(location, value);
	}

}
