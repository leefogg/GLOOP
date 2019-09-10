package gloop.graphics.rendering.shading;

import gloop.graphics.rendering.shading.materials.FullBrightShader;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.HashSet;

public final class ShaderManager {
	private static final HashSet<ShaderProgram> PROGRAMS = new HashSet<>();
	private static ShaderProgram CurrentShader;

	static {
		// TODO: More reliable way to load default shader
		try {
			CurrentShader = new FullBrightShader();
		} catch (ShaderCompilationException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static void register(ShaderProgram shader) {
		PROGRAMS.add(shader);
	}
	static void unregister(ShaderProgram shader) { PROGRAMS.remove(shader); }

	public static ShaderProgram getCurrentShader() {
		return CurrentShader;
	}
	static void setCurrentShader(ShaderProgram shader) {
		if (shader == CurrentShader)
			return;

		GL20.glUseProgram(shader.getID()); // TODO: Move this into ShaderProgram
		CurrentShader = shader;
	}

	public static void cleanup() {
		System.out.println("Deleting " + PROGRAMS.size() + " shaders...");
		for (ShaderProgram shader : PROGRAMS)
			shader.deleteProgram();
		PROGRAMS.clear();
	}
}
