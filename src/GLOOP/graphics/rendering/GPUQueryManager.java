package GLOOP.graphics.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.HashSet;

public class GPUQueryManager {
	// TODO: No point using a unique type list as register is internal
	private static final HashSet<GPUQuery> Queries = new HashSet<>();

	static void register(GPUQuery query) { Queries.add(query); System.out.println("New query object " + (Queries.size()+1));}

	static void unregister(GPUQuery query) { Queries.remove(query); }

	public static void cleanup() {
		IntBuffer queryIDs = BufferUtils.createIntBuffer(Queries.size());
		for (GPUQuery query : Queries)
			queryIDs.put(query.getID());

		queryIDs.flip();
		GL15.glDeleteQueries(queryIDs);
		Queries.clear();
	}
}
