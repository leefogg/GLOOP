package gloop.graphics.data.models;

import gloop.animation.Transform3D;
import gloop.graphics.rendering.Renderer;
import gloop.general.math.Quaternion;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.physics.data.AABB;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Model3D extends Model {
	private static final Vector3f TEMP_VECTOR = new Vector3f();
	private static final Matrix4f MODEL_MATRIX = new Matrix4f();

	protected Transform3D transform = new Transform3D();
	private final AABB boundingBox;
	private boolean isOccuder;

	public Model3D(VertexArray mesh, Material material) {
		this(mesh, material, null);
	}
	public Model3D(VertexArray mesh, Material material, AABB boundingbox) {
		super(mesh, material);
		boundingBox = boundingbox;
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

	public void getRotation(Quaternion rotation) { transform.getRotation(rotation); }
	public void setRotation(Quaternion rotation) {
		transform.setRotation(rotation);
	}

	public boolean hasBoundingBox() { return boundingBox != null; }
	public void getBoundingBox(AABB out) {
		transform.getScale(TEMP_VECTOR);
		Vector3f scale = TEMP_VECTOR;
		out.x = boundingBox.x * scale.x;
		out.y = boundingBox.y * scale.y;
		out.z = boundingBox.z * scale.z;
		out.width = boundingBox.width * scale.x;
		out.height = boundingBox.height * scale.y;
		out.depth = boundingBox.depth* scale.z;
	}

	@Override
	public boolean isOccluder() { return isOccuder; }

	public void setIsOccuder(boolean isoccuder) { isOccuder = isoccuder; }

	@Override
	public void getModelMatrix(Matrix4f out) {
		transform.getModelMatrix(out);
	}

	@Override
	public boolean isOccluded() {
		if (boundingBox == null)
			return false;

		getModelMatrix(MODEL_MATRIX);
		boolean isoutside = !Renderer.getRenderer().getScene().getGameCamera().isInsideFrustum(boundingBox, MODEL_MATRIX);
		return isoutside;
	}

	public Model3D clone() {
		//TODO: Doesn't clone transforms
		//TODO: Doesn't clone material
		return new Model3D(modelData, material, boundingBox);
	}

	//TODO: ToModel2D method
}
