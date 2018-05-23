package engine.graphics.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.HashSet;

public class RenderQueryPool {
	private static final HashSet<GPUQuery> Pool = new HashSet<>(10);

	static {
		for (int i=0; i<Pool.size(); i++)
			Pool.add(new GPUQuery(GPUQuery.Type.AnySamplesPassed));
	}

	public static GPUQuery getQuery(GPUQuery.Type type) {
		for (GPUQuery query : Pool)
			if (query.getType() == type)
				if (!query.isInUse())
					return query;
		System.out.println("Generating new query object " + (Pool.size()+1));
		return new GPUQuery(type);
	}

	static void register(GPUQuery query) { Pool.add(query); }

	static void unregister(GPUQuery query) { Pool.remove(query); }

	public static void cleanup() {
		IntBuffer queryIDs = BufferUtils.createIntBuffer(Pool.size());
		for (GPUQuery query : Pool)
			queryIDs.put(query.getID());

		queryIDs.flip();
		GL15.glDeleteQueries(queryIDs);
		Pool.clear();
	}
}
