package gloop.graphics.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.HashSet;
import java.util.Set;

public class GPUQueryManager {
	// TODO: No point using a unique type list as register is internal
	private static final Set<GPUQuery> QUERIES = new HashSet<>();

	static void register(GPUQuery query) { QUERIES.add(query); System.out.println("New query object " + (QUERIES.size()+1));}

	static void unregister(GPUQuery query) { QUERIES.remove(query); }

	public static void cleanup() {
		IntBuffer queryIDs = BufferUtils.createIntBuffer(QUERIES.size());
		for (GPUQuery query : QUERIES)
			queryIDs.put(query.getID());

		queryIDs.flip();
		GL15.glDeleteQueries(queryIDs);
		QUERIES.clear();
	}
}
