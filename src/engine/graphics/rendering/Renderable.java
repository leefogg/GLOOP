package engine.graphics.rendering;

import org.lwjgl.util.vector.Matrix4f;

public interface Renderable {
	Matrix4f getModelMatrix();
	void update(int delta, float timescaler);
	void render();
}
