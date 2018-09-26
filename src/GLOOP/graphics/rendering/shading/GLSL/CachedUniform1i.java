package GLOOP.graphics.rendering.shading.GLSL;

import GLOOP.graphics.rendering.shading.ShaderProgram;

public class CachedUniform1i extends Uniform1i {
	private int currentValue = 0;

	public CachedUniform1i(ShaderProgram program, String name) {
		super(program, name);
	}

	@Override
	public void set(Integer value) { load(location, value); }
	@Override
	public void set(int value) {
		if (value == currentValue)
			return;

		load(location, value);
		currentValue = value;
	}
}
