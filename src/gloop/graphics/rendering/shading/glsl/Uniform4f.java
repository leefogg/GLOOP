package gloop.graphics.rendering.shading.glsl;

import gloop.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector4f;

public final class Uniform4f extends Uniform<Vector4f> {

	public Uniform4f(ShaderProgram program, String name) {
		super(program, name);
	}

	@Override
	public void set(Vector4f vector) {
		set(vector.x, vector.y, vector.z, vector.w);
	}
	public void set(float x, float y, float z, float w) {
		load(location, x,y,z,w);
	}

}
