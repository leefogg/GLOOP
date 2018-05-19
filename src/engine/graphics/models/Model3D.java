package engine.graphics.models;

import engine.animation.Transform3D;
import engine.graphics.rendering.Renderer;
import engine.math.Quaternion;
import engine.graphics.shading.materials.Material;
import engine.physics.data.AABB;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Model3D extends Model {
	private static final Matrix4f ModelMatrix = new Matrix4f();

	protected Transform3D transform = new Transform3D();
	private AABB BoundingBox;

	public Model3D(VertexArray mesh, Material material) {
		this(mesh, material, null);
	}
	public Model3D(VertexArray mesh, Material material, AABB boundingbox) {
		super(mesh, material);
		BoundingBox = boundingbox;
	}


	public void setPosition(Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	public void setPosition(float x, float y, float z) {
		transform.setPosition(x, y, z);
	}
	public void getPostition(Vector3f position) {
		transform.getPosition(position);
	}

	public void setScale(Vector3f scale) {
		setScale(scale.x, scale.y, scale.z);
	}
	public void setScale(float x, float y, float z) {
		transform.setScale(x,y,z);
	}
	public void getScale(Vector3f scale) {
		transform.getScale(scale);
	}

	public void getRotation(Quaternion rotation) {
		transform.getRotation(rotation);
	}
	public void setRotation(Quaternion rotation) {
		transform.setRotation(rotation);
	}

	public AABB getBoundingBox() { return BoundingBox; }

	@Override
	public void getModelMatrix(Matrix4f out) {
		transform.getModelMatrix(out);
	}

	@Override
	protected boolean isOccuded() {
		getModelMatrix(ModelMatrix);
		boolean isoutside = !Renderer.getRenderer().getScene().currentCamera.isInsideFrustum(BoundingBox, ModelMatrix);
		return isoutside;
	}



	public Model3D clone() {
		//TODO: Doesn't clone transforms
		//TODO: Doesn't clone material
		return new Model3D(modelData, material, BoundingBox);
	}

	//TODO: ToModel2D method
}
