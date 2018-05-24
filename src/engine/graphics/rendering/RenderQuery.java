package engine.graphics.rendering;

import engine.graphics.models.Model;

class RenderQuery {
	Model Model;
	GPUQuery Query;

	public RenderQuery(Model model, GPUQuery query) {
		Model = model;
		Query = query;
	}
}
