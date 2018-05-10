package engine.graphics.models;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.HashSet;

public final class VertexArrayManager {

	private static final HashSet<VertexArray> VAOs = new HashSet<>();

	public static VertexArray getVAO(String path) throws IOException {
		// Check exists on disk
		File texturepath = Paths.get(path).toFile();
		if (!texturepath.exists())
			throw new FileNotFoundException();

		System.out.print("Loading new VAO for file \""+path+"\"... ");
		VertexArray existingVAO = findExisting(path);
		if (existingVAO != null) {
			System.out.println("VAO already loaded.");
			return existingVAO;
		}

		return null;
	}
	public static VertexArray getVAO(String name, float[] data, boolean textureprovided, boolean normalprovided, boolean tangentprovided) { //TODO: Replace with geometry
		System.out.print("Loading new VAO for file \""+name+"\"... ");
		VertexArray existingVAO = findExisting(name);
		if (existingVAO != null) {
			System.out.println("VAO already loaded.");
			return existingVAO;
		}

		// None found
		System.out.println("First load. Returning new.");
		// Load
		VertexArray newvao = new VertexArray(name); // Saved automatically
		newvao.storeStriped(data, textureprovided, normalprovided, tangentprovided);

		return newvao;
	}

	private static VertexArray findExisting(String name) {
		for (VertexArray vao : VAOs)
			if (vao.getName().contentEquals(name))
				return vao;

		return null;
	}

 	static void register(VertexArray vao) {
		VAOs.add(vao);
	}

	static void unregister(VertexArray vao) {
		VAOs.remove(vao);
	}

	public static void cleanup() {
		System.out.println("Deleting " + VAOs.size() + " VAOs..");
		IntBuffer vaoIDs = BufferUtils.createIntBuffer(VAOs.size());
		for (VertexArray vao : VAOs)
			vaoIDs.put(vao.getID());

		vaoIDs.flip();
		GL30.glDeleteVertexArrays(vaoIDs);

		VAOs.clear(); // Unregister all
	}
}
