package gloop.graphics.rendering;

import gloop.graphics.data.models.Model;
import org.lwjgl.opengl.GL11;

class RenderQuery {
	Model model;
	private final GPUQuery query = new GPUQuery(GPUQuery.Type.AnySamplesPassed);

	public RenderQuery(Model model) {
		this.model = model;
	}

	public void start() {
		query.start();
	}

	public void end() {
		query.end();
	}

	public boolean isRunning() {
		return query.isInUse();
	}

	public boolean isResultAvailable() {
		return query.isResultReady();
	}

	public boolean isModelVisible() {
		return query.getResult() == GL11.GL_TRUE;
	}
}
