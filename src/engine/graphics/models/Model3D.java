package engine.graphics.models;

import engine.animation.Transform3D;
import engine.math.Quaternion;
import engine.graphics.shading.materials.Material;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class Model3D extends Model {
	protected Transform3D transform = new Transform3D();

	//TODO: Move these to VAO constructors
	//TODO: Replace these constructors with one that takes a VAO
	public Model3D(String geometrypath, Material material) throws IOException {
		this(VertexArrayManager.newVAO(geometrypath), material);
	}
	public Model3D(VertexArray mesh, Material material) {
		super(mesh, material);
	}


	public void setPosition(Vector3f pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	public void setPosition(float x, float y, float z) {
		transform.setPosition(x, y, z);
	}
	public Vector3f getPostition(Vector3f position) {
		return transform.getPostition(position);
	}

	public void setScale(Vector3f scale) {
		setScale(scale.x, scale.y, scale.z);
	}
	public void setScale(float x, float y, float z) {
		transform.setScale(x,y,z);
	}
	public Vector3f getScale(Vector3f scale) {
		return transform.getScale(scale);
	}

	public Quaternion getRotation(Quaternion rotation) {
		rotation.set(transform.getRotation());
		return rotation;
	}
	public void setRotation(Quaternion rotation) {
		transform.setRotation(rotation);
	}

	@Override
	public Matrix4f getModelMatrix() {
		return transform.getModelMatrix();
	}

	//TODO: ToModel2D method
}
