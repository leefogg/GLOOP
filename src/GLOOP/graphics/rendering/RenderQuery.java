package GLOOP.graphics.rendering;

import GLOOP.graphics.data.models.Model;
import org.lwjgl.opengl.GL11;

class RenderQuery {
	Model Model;
	private GPUQuery Query = new GPUQuery(GPUQuery.Type.AnySamplesPassed);

	public RenderQuery(Model model) {
		Model = model;
	}

	public void start() {
		Query.start();
	}

	public void end() {
		Query.end();
	}

	public boolean isRunning() {
		return Query.isInUse();
	}

	public boolean isResultAvailable() {
		return Query.isResultReady();
	}

	public boolean isModelVisible() {
		return Query.getResult() == GL11.GL_TRUE;
	}
}
