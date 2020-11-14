package gloop.graphics.cameras;

import gloop.general.Lazy;
import gloop.general.math.MathFunctions;
import gloop.physics.data.AABB;
import org.lwjgl.util.vector.*;

public abstract class Camera {
	private static final Matrix4f INVERSEVP = new Matrix4f();
	public static final float
		DEFAULT_ZNEAR = 0.01F,
		DEFAULT_ZFAR = 1000;
	private static final Matrix4f MVP_MATRIX = new Matrix4f();
	private static final Vector4f[] AABB_VERTS = new Vector4f[8]; // For Frustum cull

	protected float znear, zfar;
	protected Lazy<Matrix4f> viewMatrix, projectionMatrix;
	protected Vector3f
		position = new Vector3f(),
		rotation = new Vector3f(); // TODO: Change to quaternion

	public Camera() {
		viewMatrix = new Lazy<>(viewmatrix -> MathFunctions.createViewMatrix(position, rotation, viewmatrix));
		projectionMatrix = new Lazy<>(projectionMatrix -> updateProjectionMatrix(projectionMatrix));
	}

	public void update(float delta, float timescaler) {}

	public void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);

		viewMatrix.expire();
	}
	public void getPosition(Vector3f clone) {
		clone.set(position);
	}

	public void getRotation(Vector3f clone) { clone.set(rotation); }

	public void setRotation(Vector3f rotation) {
		setRotation(rotation.x, rotation.y, rotation.z);
	}
	public void setRotation(float pitch, float yaw, float roll) {
		rotation.set(pitch, yaw, roll);

		viewMatrix.expire();
	}

	public void setznear(float znear) {
		this.znear = znear;

		projectionMatrix.expire();
	}
	public float getznear() {
		return znear;
	}

	public void setzfar(float zfar) {
		this.zfar = zfar;

		projectionMatrix.expire();
	}
	public float getzfar() {
		return zfar;
	}

	// Note: this does not change Rotation field
	public void lookAt(Vector3f target) {
		viewMatrix.set(MathFunctions.createViewMatrix(position, target, new Vector3f(0,1,0), viewMatrix.get()));
	}

	public Matrix4f getProjectionMatrix() { return projectionMatrix.get(); }
	protected abstract Matrix4f updateProjectionMatrix(Matrix4f projectionMatrix);

	public Matrix4f getViewMatrix() { return viewMatrix.get(); }

	public final boolean isInsideFrustum(AABB aabb, Matrix4f modelmatrix) {
		Matrix4f.mul(getProjectionMatrix(), getViewMatrix(), MVP_MATRIX);
		Matrix4f.mul(MVP_MATRIX, modelmatrix, MVP_MATRIX);

		AABB_VERTS[0] = transform(MVP_MATRIX,    aabb.x,               aabb.y,                aabb.z,             AABB_VERTS[0]);
		AABB_VERTS[1] = transform(MVP_MATRIX, aabb.x+aabb.width,    aabb.y,                aabb.z,             AABB_VERTS[1]);
		AABB_VERTS[2] = transform(MVP_MATRIX,    aabb.x,            aabb.y+aabb.height,    aabb.z,             AABB_VERTS[2]);
		AABB_VERTS[3] = transform(MVP_MATRIX, aabb.x+aabb.width, aabb.y+aabb.height,    aabb.z,             AABB_VERTS[3]);
		AABB_VERTS[4] = transform(MVP_MATRIX,    aabb.x,               aabb.y,             aabb.z+aabb.depth,  AABB_VERTS[4]);
		AABB_VERTS[5] = transform(MVP_MATRIX, aabb.x+aabb.width,    aabb.y,             aabb.z+aabb.depth,  AABB_VERTS[5]);
		AABB_VERTS[6] = transform(MVP_MATRIX,    aabb.x,            aabb.y+aabb.height, aabb.z+aabb.depth,  AABB_VERTS[6]);
		AABB_VERTS[7] = transform(MVP_MATRIX, aabb.x+aabb.width, aabb.y+aabb.height, aabb.z+aabb.depth,  AABB_VERTS[7]);

		// Check verts against all view planes
		int c1 = 0,
			c2 = 0,
			c3 = 0,
			c4 = 0,
			c5 = 0,
			c6 = 0;
		for (Vector4f vert : AABB_VERTS) {
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

	public void getFrustumVerts(Vector3f[] verts) {
		if (verts.length < 8)
			throw new IllegalArgumentException("Provided vertex array not long enough to store 8 vertcies");

		verts[0].set(-1,-1,1);  // Far top left
		verts[1].set(1,-1,1);   // Far top right
		verts[2].set(1,1,1);    // Far bottom right
		verts[3].set(-1,1,1);   // Far bottom left
		verts[4].set(-1,-1,0);  // Near top left
		verts[5].set(1,-1,0);   // Near top right
		verts[6].set(1,1,0);    // Near bottom right
		verts[7].set(-1,1,0);   // Near bottom left

		Matrix4f.invert(getProjectionMatrix(), INVERSEVP);
		Vector4f temp = new Vector4f();
		for (Vector3f vert : verts) {
			temp.set(vert.x, vert.y, vert.z, 1);
			Matrix4f.transform(INVERSEVP, temp, temp);
			float inverse = 1f / temp.w;
			vert.set(temp.x * inverse, temp.y * inverse, -temp.z * inverse);
		}
	}

	public void getViewDirection(WritableVector3f out) {
		Matrix4f viewmatrix = getViewMatrix();
		// TODO: Make getForward(), getLeft(), getUp() and getPosition() in matrix
		out.set(viewmatrix.m02, viewmatrix.m12, viewmatrix.m22);
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
}
