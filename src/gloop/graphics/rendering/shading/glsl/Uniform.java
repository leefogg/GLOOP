package gloop.graphics.rendering.shading.glsl;

import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.*;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

abstract class Uniform<T> {
	private static final FloatBuffer // pass-though buffers
		MATRIX_3F_BUFFER = BufferUtils.createFloatBuffer(3 * 3),
		MATRIX_4F_BUFFER = BufferUtils.createFloatBuffer(4 * 4);

	protected final int location;

	public Uniform(ShaderProgram program, CharSequence name) {
		location = getUniformLocation(program.getID(), name);
		Renderer.checkErrors();
	}

	public abstract void set(T value);


	protected static void load(int location, Vector4f vector) {
		load(location, vector.x, vector.y, vector.z, vector.w);
	}
	protected static void load(int location, float x, float y, float z, float w) {
		glUniform4f(location, x, y, z, w);
	}
	protected static void load(int location, Vector3f vector) {
		load(location, vector.x, vector.y, vector.z);
	}
	protected static void load(int location, float x, float y, float z) {
		glUniform3f(location, x, y, z);
	}
	protected static void load(int location, Vector2f vector) {
		load(location, vector.x, vector.y);
	}
	protected static void load(int location, float x, float y) {
		glUniform2f(location, x, y);
	}
	protected static void load(int location, float value) {
		glUniform1f(location, value);
	}
	protected static void load(int location, int value) { glUniform1i(location, value); }
	protected static void load(int location, boolean value) {
		glUniform1f(location, value ? 1 : 0);
	}
	protected static void load(int location, Matrix3f matrix) {
		matrix.store(MATRIX_3F_BUFFER);
		MATRIX_3F_BUFFER.flip(); // Flip to read mode
		glUniformMatrix4(location, false, MATRIX_3F_BUFFER);
	}
	protected static void load(int location, Matrix4f matrix) {
		matrix.store(MATRIX_4F_BUFFER);
		MATRIX_4F_BUFFER.flip(); // Flip to read mode
		glUniformMatrix4(location, false, MATRIX_4F_BUFFER);
	}

	private static int getUniformLocation(int programID, CharSequence uniformName) {
		int location = glGetUniformLocation(programID, uniformName);

		if (location == -1)
			System.err.println("Uniform \"" + uniformName + "\" not found!");

		return location;
	}

}
