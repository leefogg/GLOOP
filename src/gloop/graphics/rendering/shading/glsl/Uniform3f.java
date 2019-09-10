package gloop.graphics.rendering.shading.glsl;

import gloop.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector3f;

public final class Uniform3f extends Uniform<Vector3f> {

	public Uniform3f(ShaderProgram shader, String name) {
		super(shader, name);
	}

	@Override
	public void set(Vector3f vector) {
		set(vector.x, vector.y, vector.z);
	}

	public void set(float x, float y, float z) {
		load(location, x,y,z);
	}

}
