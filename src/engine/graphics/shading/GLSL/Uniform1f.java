package engine.graphics.shading.GLSL;

import engine.graphics.shading.ShaderProgram;

public final class Uniform1f extends Uniform<Float> {

	public Uniform1f(ShaderProgram shader, String name) {
		super(shader, name);
	}

	@Override
	public void set(Float value) {
		load(location, value);
	}
	public void set(float value) {
		load(location, value);
	}
}
