package gloop.graphics.rendering;

import gloop.graphics.data.models.Model;

import java.util.ArrayList;
import java.util.List;

public class RenderQueryPool {
	private static final List<RenderQuery> POOL = new ArrayList<>();
	private static final List<RenderQuery> PENDING_QUERIES_TEMP = new ArrayList<>();
	private static int NextQuery = 0;

	RenderQueryPool(int initialsize) {
		for (int i=0; i<initialsize; i++)
			POOL.add(new RenderQuery(null));
	}

	public RenderQuery startQuery(Model model) {
		RenderQuery renderquery = getQuery(model);
		renderquery.model = model;
		renderquery.start();
		return renderquery;
	}

	private RenderQuery getQuery(Model model) {
		int start = NextQuery;
		NextQuery++;
		NextQuery = NextQuery % POOL.size();


		// Find the next available query
		// Emulate a ring buffer without using modulus
		int i = start;
		for (; i< POOL.size(); i++) {
			RenderQuery query = POOL.get(i);
			if (!query.isRunning())
				return query;
		}
		for (i = 0; i<start; i++) {
			RenderQuery query = POOL.get(i);
			if (!query.isRunning())
				return query;
		}

		RenderQuery newquery =  new RenderQuery(model);
		POOL.add(newquery);
		return newquery;
	}

	//TODO: Return readonly list
	public List<RenderQuery> getPendingQueries() {
		PENDING_QUERIES_TEMP.clear();
		PENDING_QUERIES_TEMP.addAll(POOL);
		return PENDING_QUERIES_TEMP;
	}

	public boolean isModelPending(Model model) {
		for (int i = 0; i< POOL.size(); i++) {
			RenderQuery renderquery = POOL.get(i);
			if (renderquery.isRunning())
				if (renderquery.model == model)
					return true;
		}

		return false;
	}
}
