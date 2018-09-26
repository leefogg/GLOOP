package GLOOP.graphics.rendering;

import GLOOP.graphics.data.models.Model;

import java.util.ArrayList;
import java.util.List;

public class RenderQueryPool {
	private static ArrayList<RenderQuery> Pool = new ArrayList<>();
	private static ArrayList<RenderQuery> PendingQueriesTemp = new ArrayList<>();
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


		// Find the next available query
		// Emulate a ring buffer without using modulus
		int i = start;
		for (; i<Pool.size(); i++) {
			RenderQuery query = Pool.get(i);
			if (!query.isRunning())
				return query;
		}
		for (i = 0; i<start; i++) {
			RenderQuery query = Pool.get(i);
			if (!query.isRunning())
				return query;
		}

		RenderQuery newquery =  new RenderQuery(model);
		Pool.add(newquery);
		return newquery;
	}

	public List<RenderQuery> getPendingQueries() {
		PendingQueriesTemp.clear();
		PendingQueriesTemp.addAll(Pool);
		return PendingQueriesTemp;
	}

	public boolean isModelPending(Model model) {
		for (int i=0; i<Pool.size(); i++) {
			RenderQuery renderquery = Pool.get(i);
			if (renderquery.isRunning())
				if (renderquery.Model == model)
					return true;
		}

		return false;
	}
}
