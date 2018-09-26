package GLOOP.graphics.data.models;

import GLOOP.graphics.rendering.Renderable;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.shading.FragmentShader;
import GLOOP.graphics.rendering.shading.VertexShader;
import GLOOP.graphics.rendering.shading.materials.BasicMaterial;
import GLOOP.graphics.rendering.shading.materials.BasicShader;
import GLOOP.graphics.rendering.shading.materials.Material;
import org.lwjgl.util.vector.Matrix4f;

public abstract class Model implements Renderable {
	public enum Visibility {
		Unknown,
		NotVisible,
		Visible,
	}

	private final static BasicMaterial ERROR_MATERIAL;
	static {
		String vertshadercode =
				"#version 150\n" +
				"in vec3 Position;" +
				"uniform mat4 VPMatrix, ModelMatrix;" +
				"void main(void) {" +
				"gl_Position = VPMatrix * ModelMatrix * vec4(Position, 1);" +
				"}";
		VertexShader vertshader = new VertexShader(vertshadercode);
		String fragshadercode =
				"#version 150\n" +
				"out vec3 fragColor;" +
				"void main(void) {" +
				"fragColor = vec3(1.0,0.0,1.0);" +
				"}";
		FragmentShader fragshader = new FragmentShader(fragshadercode);
		BasicShader shader = new BasicShader(vertshader, fragshader);
		ERROR_MATERIAL = new BasicMaterial<>(shader);
	}

	protected final VertexArray modelData;
	protected Material material;
	private Matrix4f modelMatrix = new Matrix4f();
	private Visibility isVisible = Visibility.Unknown;

	private boolean hidden = false;

	public Model(VertexArray mesh, Material material) {
		this.modelData = mesh;
		this.material = material;

		Renderer.checkErrors();
	}

	public void setMaterial(Material material) {
		this.material = material;
	}
	public Material getMaterial() { return material; }

	@Override
 	public void update(float delta, float timescaler) {}

	public final boolean isHidden() { return hidden; }
	public final void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isOccluder() { return false; }
	public boolean isOccluded() { return false; }

	public VertexArray getMeshBuffer() { return modelData; }

	public int getNumberOfVertcies() {
		return (modelData.isIndexed()) ? modelData.getNumberOfIndices() : modelData.getNumberofVertcies();
	}

	public void setVisibility(Visibility isvisible) { isVisible = isvisible; }
	public Visibility visibility() { return isVisible; }

	@Override
	public void render() {
		if (modelData.isDisposed())
			return;
		if (isHidden())
			return;

		Material materialtouse = material.getShader().isDisposed() ? ERROR_MATERIAL : material;
		materialtouse.bind();
		materialtouse.commit();
		getModelMatrix(modelMatrix);
		materialtouse.setCameraAttributes(Renderer.getCurrentCamera(), modelMatrix);

		modelData.render();
	}
}
