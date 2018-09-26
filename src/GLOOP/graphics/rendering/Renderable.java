package GLOOP.graphics.rendering;

import org.lwjgl.util.vector.Matrix4f;

public interface Renderable {
	void getModelMatrix(Matrix4f out); // TODO: Shouldn't be here
	void update(float delta, float timescaler); // TODO: Move to Updatable Interface
	void render();
}
