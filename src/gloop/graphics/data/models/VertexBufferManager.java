package gloop.graphics.data.models;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.HashSet;

public abstract class VertexBufferManager {
	private static final HashSet<VertexBuffer> VBOS = new HashSet<>();

	static void register(VertexBuffer vbo) {
		VBOS.add(vbo);
	}

	static void unregister(VertexBuffer vbo) {
		VBOS.remove(vbo);
	}

	static void deleteVBO(int ID) {
		GL15.glDeleteBuffers(ID);
	}

	public static int getVBOCount() {
		return VBOS.size();
	}

	public static void cleanup() {
		System.out.println("Deleting " + VBOS.size() + " VBOs..");
		IntBuffer vaoIDs = BufferUtils.createIntBuffer(VBOS.size());
		for (VertexBuffer vao : VBOS)
			vaoIDs.put(vao.getID());

		vaoIDs.flip();
		GL15.glDeleteBuffers(vaoIDs);

		VBOS.clear(); // Unregister all
	}
}
