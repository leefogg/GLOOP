package engine.graphics.rendering;

import org.lwjgl.util.vector.Matrix4f;

public interface Renderable {
	void getModelMatrix(Matrix4f out);
	void update(int delta, float timescaler);
	void render();
}
