package engine.graphics.rendering;

import engine.graphics.models.Model;

import java.util.ArrayList;
import java.util.List;

public class RenderQueryPool {
	private static ArrayList<RenderQuery> Pool = new ArrayList<>();
	private static int NextQuery = 0;

	RenderQueryPool(int initialsize) {
		for (int i=0; i<initialsize; i++)
			Pool.add(new RenderQuery(null));
	}

	public RenderQuery startQuery(Model model) {
		RenderQuery renderquery = getQuery(model);
		renderquery.Model = model;
		renderquery.start();
		return renderquery;
	}

	private RenderQuery getQuery(Model model) {
		int start = NextQuery;
		NextQuery++;
		NextQuery = NextQuery % Pool.size();

		int i = start;
		for (; i<Pool.size(); i++) {
			RenderQuery query = isPoolIndexAvailable(i);
			if (query != null)
				return query;
		}
		for (i = 0; i<start; i++) {
			RenderQuery query = isPoolIndexAvailable(i);
			if (query != null)
				return query;
		}

		System.out.println("new RenderQuery");
		RenderQuery newquery =  new RenderQuery(model);
		Pool.add(newquery);
		return newquery;
	}

	public List<RenderQuery> getPendingQueries() { return Pool; }

	private RenderQuery isPoolIndexAvailable(int i) {
		RenderQuery query = Pool.get(i);
		if (!query.isRunning())
			return query;
		return null;
	}


}
