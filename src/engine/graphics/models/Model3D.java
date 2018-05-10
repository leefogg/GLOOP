package engine.graphics.models;

import engine.animation.Transform3D;
import engine.graphics.cameras.Camera;
import engine.graphics.rendering.Renderer;
import engine.math.Quaternion;
import engine.graphics.shading.materials.Material;
import engine.physics.data.AABB;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Model3D extends Model {
	private static final Matrix4f MVPMatrix = new Matrix4f();
	private static final Matrix4f ModelMatrix = new Matrix4f();
	private static final Vector4f[] AABBVerts = new Vector4f[8]; // For Frustum cull

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
		Camera camera = Renderer.getRenderer().getScene().currentCamera;
		Matrix4f projectionmatrix = camera.getProjectionMatrix();
		Matrix4f viewmatrix = camera.getViewMatrix();
		Matrix4f.mul(projectionmatrix, viewmatrix, MVPMatrix);
		getModelMatrix(ModelMatrix);
		Matrix4f.mul(MVPMatrix, ModelMatrix, MVPMatrix);

		return !isInsideFrustum(BoundingBox, MVPMatrix);
	}

	private static final boolean isInsideFrustum(AABB aabb, Matrix4f mvpMatrix) {
		AABBVerts[0] = transform(mvpMatrix,    aabb.x,               aabb.y,                aabb.z,             AABBVerts[0]);
		AABBVerts[1] = transform(mvpMatrix, aabb.x+aabb.width,    aabb.y,                aabb.z,             AABBVerts[1]);
		AABBVerts[2] = transform(mvpMatrix,    aabb.x,            aabb.y+aabb.height,    aabb.z,             AABBVerts[2]);
		AABBVerts[3] = transform(mvpMatrix, aabb.x+aabb.width, aabb.y+aabb.height,    aabb.z,             AABBVerts[3]);
		AABBVerts[4] = transform(mvpMatrix,    aabb.x,               aabb.y,             aabb.z+aabb.depth,  AABBVerts[4]);
		AABBVerts[5] = transform(mvpMatrix, aabb.x+aabb.width,    aabb.y,             aabb.z+aabb.depth,  AABBVerts[5]);
		AABBVerts[6] = transform(mvpMatrix,    aabb.x,            aabb.y+aabb.height, aabb.z+aabb.depth,  AABBVerts[6]);
		AABBVerts[7] = transform(mvpMatrix, aabb.x+aabb.width, aabb.y+aabb.height, aabb.z+aabb.depth,  AABBVerts[7]);

		// Check verts against all view planes
		int c1 = 0,
				c2 = 0,
				c3 = 0,
				c4 = 0,
				c5 = 0,
				c6 = 0;
		for (Vector4f vert : AABBVerts) {
			if (vert.x < -vert.w)
				c1++;
			if (vert.x > vert.w)
				c2++;
			if (vert.y < -vert.w)
				c3++;
			if (vert.y > vert.w)
				c4++;
			if (vert.z < -vert.w)
				c5++;
			if (vert.z > vert.w)
				c6++;
		}
		return !(c1 == 8 || c2 == 8 || c3 == 8 || c4 == 8 || c5 == 8 || c6 == 8);
	}

	public static Vector4f transform(Matrix4f left, float x, float y, float z, Vector4f dest) {
		if (dest == null)
			dest = new Vector4f();

		// Note: Removed " * W" from end as W was always 1 and this is performant code
		dest.x = left.m00 * x + left.m10 * y + left.m20 * z + left.m30;
		dest.y = left.m01 * x + left.m11 * y + left.m21 * z + left.m31;
		dest.z = left.m02 * x + left.m12 * y + left.m22 * z + left.m32;
		dest.w = left.m03 * x + left.m13 * y + left.m23 * z + left.m33;
		return dest;
	}

	public Model3D clone() {
		//TODO: Doesn't clone transforms
		//TODO: Doesn't clone material
		return new Model3D(modelData, material);
	}

	//TODO: ToModel2D method
}
