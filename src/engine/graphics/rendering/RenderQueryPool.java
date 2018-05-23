package engine.graphics.rendering;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.nio.IntBuffer;
import java.util.ArrayList;

public class RenderQueryPool {
	private static final ArrayList<GPUQuery> Pool = new ArrayList<>(10);
	private static int NextQuery = 0;

	static {
		for (int i=0; i<10; i++)
			Pool.add(new GPUQuery(GPUQuery.Type.AnySamplesPassed));
	}

	public static GPUQuery getQuery(GPUQuery.Type type) {
		int start = NextQuery;
		NextQuery++;
		NextQuery = NextQuery % Pool.size();

		int i = start;
		for (; i<Pool.size(); i++) {
			GPUQuery query = isPoolIndexAvailable(i, type);
			if (query != null)
				return query;
		}
		for (i = 0; i<start; i++) {
			GPUQuery query = isPoolIndexAvailable(i, type);
			if (query != null)
				return query;
		}


		return new GPUQuery(type);
	}

	private static GPUQuery isPoolIndexAvailable(int i, GPUQuery.Type type) {
		GPUQuery query = Pool.get(i);
		if (query.getType() == type)
			if (!query.isInUse())
				return query;
		return null;
	}

	static void register(GPUQuery query) { Pool.add(query); System.out.println("Generating new query object " + (Pool.size()+1));}

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
