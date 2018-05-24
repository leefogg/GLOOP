package engine.graphics.rendering;

import engine.graphics.models.Model;

import java.util.ArrayList;
import java.util.List;

public class RenderQueryPool {
	private static ArrayList<RenderQuery> Pool = new ArrayList<>();
	private static int NextQuery = 0;

	RenderQueryPool(int initialsize) {
		for (int i=0; i<initialsize; i++)
			Pool.add(new RenderQuery(null, new GPUQuery(GPUQuery.Type.AnySamplesPassed)));
	}

	public RenderQuery startQuery(Model model) {
		RenderQuery renderquery = getQuery(GPUQuery.Type.AnySamplesPassed, model);
		renderquery.Model = model;
		renderquery.Query.start();
		return renderquery;
	}

	private RenderQuery getQuery(GPUQuery.Type type, Model model) {
		int start = NextQuery;
		NextQuery++;
		NextQuery = NextQuery % Pool.size();

		int i = start;
		for (; i<Pool.size(); i++) {
			RenderQuery query = isPoolIndexAvailable(i, type);
			if (query != null)
				return query;
		}
		for (i = 0; i<start; i++) {
			RenderQuery query = isPoolIndexAvailable(i, type);
			if (query != null)
				return query;
		}

		System.out.println("new RenderQuery");
		RenderQuery newquery =  new RenderQuery(model, new GPUQuery(type));
		Pool.add(newquery);
		return newquery;
	}

	public List<RenderQuery> getPendingQueries() { return Pool; }

	private RenderQuery isPoolIndexAvailable(int i, GPUQuery.Type type) {
		RenderQuery query = Pool.get(i);
		if (query.Query.getType() == type)
			if (!query.Query.isInUse())
				return query;
		return null;
	}


}
