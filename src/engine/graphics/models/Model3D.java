package engine.graphics.models;

import engine.animation.Transform3D;
import engine.graphics.rendering.Renderer;
import engine.math.Quaternion;
import engine.graphics.shading.materials.Material;
import engine.physics.data.AABB;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Model3D extends Model {
	private static final Vector3f TempVector = new Vector3f();
	private static final Matrix4f ModelMatrix = new Matrix4f();

	protected Transform3D transform = new Transform3D();
	private AABB BoundingBox;
	private boolean IsOccuder;

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

	public void getBoundingBox(AABB out) {
		transform.getScale(TempVector);
		Vector3f scale = TempVector;
		out.x = BoundingBox.x * scale.x;
		out.y = BoundingBox.y * scale.y;
		out.z = BoundingBox.z * scale.z;
		out.width = BoundingBox.width * scale.x;
		out.height = BoundingBox.height * scale.y;
		out.depth = BoundingBox.depth* scale.z;
	}

	@Override
	public boolean isOccluder() { return IsOccuder; }

	public void setIsOccuder(boolean isoccuder) { IsOccuder = isoccuder; }

	@Override
	public void getModelMatrix(Matrix4f out) {
		transform.getModelMatrix(out);
	}

	@Override
	public boolean isOccuded() {
		if (BoundingBox == null)
			return false;

		getModelMatrix(ModelMatrix);
		boolean isoutside = !Renderer.getRenderer().getScene().getGameCamera().isInsideFrustum(BoundingBox, ModelMatrix);
		return isoutside;
	}

	public Model3D clone() {
		//TODO: Doesn't clone transforms
		//TODO: Doesn't clone material
		return new Model3D(modelData, material, BoundingBox);
	}

	//TODO: ToModel2D method
}
