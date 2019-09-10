package gloop.graphics.rendering.shading.glsl;

import gloop.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector2f;


public final class Uniform2f extends Uniform<Vector2f> {

	public Uniform2f(ShaderProgram shader, String name) {
		super(shader, name);
	}

	@Override
	public void set(Vector2f vector) {
		set(vector.x, vector.y);
	}

	public void set(float x, float y) {
		load(location, x,y);
	}
}

