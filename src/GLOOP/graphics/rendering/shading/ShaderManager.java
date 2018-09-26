package GLOOP.graphics.rendering.shading;

import GLOOP.graphics.rendering.shading.materials.FullBrightShader;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.util.HashSet;

public final class ShaderManager {
	private static final HashSet<ShaderProgram> shaders = new HashSet<>();
	private static ShaderProgram currentShader;

	static {
		// TODO: More reliable way to load default shader
		try {
			currentShader = new FullBrightShader();
		} catch (ShaderCompilationException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	static void register(ShaderProgram shader) {
		shaders.add(shader);
	}
	static void unregister(ShaderProgram shader) { shaders.remove(shader); }

	public static ShaderProgram getCurrentShader() {
		return currentShader;
	}
	static void setCurrentShader(ShaderProgram shader) {
		if (shader == currentShader)
			return;

		GL20.glUseProgram(shader.getID()); // TODO: Move this into ShaderProgram
		currentShader = shader;
	}

	public static void cleanup() {
		System.out.println("Deleting " + shaders.size() + " shaders...");
		for (ShaderProgram shader : shaders)
			shader.deleteProgram();
		shaders.clear();
	}
}
