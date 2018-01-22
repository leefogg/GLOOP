package engine.graphics.models;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.HashSet;

public final class VertexBufferManager {
	private static final HashSet<VertexBuffer> VBOs = new HashSet<>();

	static void register(VertexBuffer vbo) {
		VBOs.add(vbo);
	}

	static void unregister(VertexBuffer vbo) {
		VBOs.remove(vbo);
	}

	static void deleteVBO(int ID) {
		GL15.glDeleteBuffers(ID);
	}

	public static final int getVBOCount() {
		return VBOs.size();
	}

	public static void cleanup() {
		System.out.println("Deleting " + VBOs.size() + " VBOs..");
		IntBuffer vaoIDs = BufferUtils.createIntBuffer(VBOs.size());
		for (VertexBuffer vao : VBOs)
			vaoIDs.put(vao.getID());

		vaoIDs.flip();
		GL15.glDeleteBuffers(vaoIDs);

		VBOs.clear(); // Unregister all
	}
}
