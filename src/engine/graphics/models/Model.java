package engine.graphics.models;

import engine.graphics.rendering.Renderable;
import engine.graphics.rendering.Renderer;
import engine.graphics.shading.ShaderManager;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.shading.materials.Material;
import org.lwjgl.util.vector.Matrix4f;

public abstract class Model implements Renderable {
	protected final VertexArray modelData;
	protected Material material;
	private Matrix4f vpmatrix = new Matrix4f();

	private boolean hidden = false;

	public Model(VertexArray mesh, Material material) {
		this.modelData = mesh;
		this.material = material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	public Material getMaterial() {
		return material;
	}

	@Override
 	public void update(int delta, float timescaler) {}

	public final boolean isHidden() {
		return hidden;
	}
	public final void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public VertexArray getMeshBuffer() {return modelData;}

	@Override
	public void render() {
		if (modelData.isDisposed())
			return;
		if (isHidden())
			return;

		//TODO: Render using error shader if shader is disposed
		material.bind();
		material.commit();

		ShaderProgram currentshader = ShaderManager.getCurrentShader();
		engine.graphics.cameras.Camera camera = Renderer.getRenderer().getScene().currentCamera;
		Matrix4f projectionmatrix = camera.getProjectionMatrix();
		Matrix4f viewmatrix = camera.getViewMatrix();
		Matrix4f.mul(projectionmatrix, viewmatrix, vpmatrix);
		currentshader.setViewProjectionMatrix(vpmatrix);
		vpmatrix.invert();
		currentshader.setInverseViewProjectionMatrix(vpmatrix);
		currentshader.setModelMatrix(getModelMatrix());

		modelData.render();
	}
}
