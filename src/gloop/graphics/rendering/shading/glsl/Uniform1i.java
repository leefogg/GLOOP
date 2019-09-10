package gloop.graphics.rendering.shading.glsl;

import gloop.graphics.rendering.shading.ShaderProgram;

public class Uniform1i extends Uniform<Integer> {

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
