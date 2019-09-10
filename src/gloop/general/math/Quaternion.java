package gloop.general.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;


// Sources:
// ThinMatrix				https://github.com/TheThinMatrix/OpenGL-Skeletal-Animation/blob/master/Animation/animation/Quaternion.java
// Jeorge Rodriguez			https://github.com/BSVino/MathForGameDevelopers/blob/quaternion-transform/math/quaternion.cpp
// Opengl-tutorial.org		http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-17-quaternions/
// TODO: Port more methods from following
// MathFu 					https://github.com/google/mathfu/blob/master/include/mathfu/quaternion.h
// JMonkeyEngine			https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-core/src/main/java/com/jme3/math/Quaternion.java
// Cannon.js				https://github.com/schteppe/cannon.js/blob/master/src/math/Quaternion.js
public final class Quaternion {
	private float w = 1;
	private Vector3f v = new Vector3f(0,0,0); // TODO: Change to vector4f

	public  Quaternion() {}
	private Quaternion(float w, Vector3f vector) {
		this(vector.x, vector.y, vector.z, w);
	}
	private Quaternion(float x, float y, float z, float w) {
		this.w = w;
		this.v.x = x;
		this.v.y = y;
		this.v.z = z;
	}
	public 	Quaternion(Vector3f axis, float degrees) {
		float radians = (float)Math.toRadians(degrees);

		w = (float)Math.cos(radians/2f);

		v = (Vector3f)axis.scale((float)Math.sin(radians/2f));
	}
	/**
	 * Extracts the rotation part of a transformation matrix and converts it to
	 * a quaternion using the magic of maths.
	 *
	 * @param matrix
	 *            - the transformation matrix containing the rotation which this
	 *            quaternion shall represent.
	 */
	public Quaternion(Matrix4f matrix) {
		float diagonal = matrix.m00 + matrix.m11 + matrix.m22;
		if (diagonal > 0) {
			float w4 = (float) (Math.sqrt(diagonal + 1f) * 2f);
			w = w4 / 4f;
			v.x = (matrix.m21 - matrix.m12) / w4;
			v.y = (matrix.m02 - matrix.m20) / w4;
			v.z = (matrix.m10 - matrix.m01) / w4;
		} else if (matrix.m00 > matrix.m11 && matrix.m00 > matrix.m22) {
			float x4 = (float) (Math.sqrt(1f + matrix.m00 - matrix.m11 - matrix.m22) * 2f);
			w = (matrix.m21 - matrix.m12) / x4;
			v.x = x4 / 4f;
			v.y = (matrix.m01 + matrix.m10) / x4;
			v.z = (matrix.m02 + matrix.m20) / x4;
		} else if (matrix.m11 > matrix.m22) {
			float y4 = (float) (Math.sqrt(1f + matrix.m11 - matrix.m00 - matrix.m22) * 2f);
			w = (matrix.m02 - matrix.m20) / y4;
			v.x = (matrix.m01 + matrix.m10) / y4;
			v.y = y4 / 4f;
			v.z = (matrix.m12 + matrix.m21) / y4;
		} else {
			float z4 = (float) (Math.sqrt(1f + matrix.m22 - matrix.m00 - matrix.m11) * 2f);
			w = (matrix.m10 - matrix.m01) / z4;
			v.x = (matrix.m02 + matrix.m20) / z4;
			v.y = (matrix.m12 + matrix.m21) / z4;
			v.z = z4 / 4f;
		}
		this.normalize();
	}

	Quaternion set(float x, float y, float z, float w) {
		return set(new Vector3f(x,y,z), w);
	}
	Quaternion set(Vector3f v, float w) {
		this.v = v;
		this.w = w;

		return this;
	}

	public Quaternion rotate(Vector3f rotation) {
		return rotate(rotation.x, rotation.y, rotation.z);
	}
	public Quaternion rotate(float xdegrees, float ydegrees, float zdegrees) {
		rotate(1,0,0, xdegrees);
		rotate(0,0,1, zdegrees);
		rotate(0,1,0, ydegrees);
		return this;
	}
	public Quaternion rotate(float x, float y, float z, double degrees) {
		return rotate(this, x, y, z, degrees, this);
	}
	public Quaternion rotate(Vector3f axis, double degrees) {
		return rotate(this, axis, degrees, this);
	}
	public static Quaternion rotate(Quaternion left, Vector3f axis, double degrees, Quaternion out) {
		return rotate(left, axis.x, axis.y, axis.z, degrees, out);
	}
	public static Quaternion rotate(Quaternion left, float x, float y, float z, double degrees, Quaternion out) {
		double radians = (float)Math.toRadians(degrees);
		float w = (float)Math.cos(radians/2f);
		float reversescaler = (float)Math.sin(radians/2f);

		return multiply(
				left,
				x * reversescaler,
				y * reversescaler,
				z * reversescaler,
				w,
				out
		);
	}

	private static final Vector3f vcV = new Vector3f();
	private static final Vector3f VcV2w = new Vector3f();
	public void multiply(Vector3f out) {
		Vector3f.cross(this.v, out, vcV);
		VcV2w.set(vcV).scale(2f*w);

		Vector3f.add(out, VcV2w, VcV2w);
		Vector3f.add(VcV2w, (Vector3f)Vector3f.cross(this.v, vcV, vcV).scale(2f), out);
	}
	public Quaternion multiply(Quaternion other) {
		multiply(this, other.v.x, other.v.y, other.v.z, other.w, this);

		return this;
	}
	public static Quaternion multiply(Quaternion left, Quaternion right) {
		return multiply(left, right.v.x, right.v.y, right.v.z, right.w, new Quaternion());
	}
	private static Quaternion multiply(Quaternion left, float x, float y, float z, double w, Quaternion out) {
		double xx = left.w * x + left.v.x * w + left.v.y * z - left.v.z * y;
		double yy = left.w * y + left.v.y * w + left.v.z * x - left.v.x * z;
		double zz = left.w * z + left.v.z * w + left.v.x * y - left.v.y * x;
		double ww = left.w * w - left.v.x * x - left.v.y * y - left.v.z * z;

		out.w 	= (float)ww;
		out.v.x = (float)xx;
		out.v.y = (float)yy;
		out.v.z = (float)zz;

		return out;
	}

	public static Quaternion nlerp(Quaternion a, Quaternion b, float blend)	{
		Quaternion result = new Quaternion();
		float dot = dot(a, b);
		float blendinv = 1f - blend;
		if(dot < 0.0f) {
			 float tmpfw = -b.w;
			 float tmpfx = -b.v.x;
			 float tmpfy = -b.v.y;
			 float tmpfz = -b.v.z;
			 result.w =   blendinv*a.w   + blend*tmpfw;
			 result.v.x = blendinv*a.v.x + blend*tmpfx;
			 result.v.y = blendinv*a.v.y + blend*tmpfy;
			 result.v.z = blendinv*a.v.z + blend*tmpfz;
		} else {
			 result.w =   blendinv*a.w   + blend*b.w;
			 result.v.x = blendinv*a.v.x + blend*b.v.x;
			 result.v.y = blendinv*a.v.y + blend*b.v.y;
			 result.v.z = blendinv*a.v.z + blend*b.v.z;
		}


		return result.normalize();
	}
	/**
	 * Interpolates between two quaternion rotations and returns a new
	 * quaternion which represents a rotation somewhere in between the two input
	 * rotations.
	 *
	 * @param start
	 *			- the starting rotation.
	 * @param end
	 *			- the end rotation.
	 * @param progression
	 *			- a value between 0 and 1 indicating how much to interpolate
	 *			between the two rotations. 0 would return the start rotation,
	 *			and 1 would return the end rotation.
	 * @return The interpolated rotation as a quaternion.
	 */
	public static Quaternion slerp(Quaternion start, Quaternion end, float progression) {
		start.normalize();
		end.normalize();
		final float d = dot(start, end);
		final float absDot = d < 0f ? -d : d;
		float scale0 = 1f - progression;
		float scale1 = progression;

		if (1 - absDot > 0.1f) {
			final float angle = (float) Math.acos(absDot);
			final float invSinTheta = 1f / (float) Math.sin(angle);
			scale0 = (float) Math.sin((1f - progression) * angle) * invSinTheta;
			scale1 = (float) Math.sin(progression * angle) * invSinTheta;
		}

		if (d < 0f)
			scale1 = -scale1;

		float newX = scale0 * start.v.x + scale1 * end.v.x;
		float newY = scale0 * start.v.y + scale1 * end.v.y;
		float newZ = scale0 * start.v.z + scale1 * end.v.z;
		float newW = scale0 * start.w + scale1 * end.w;
		return new Quaternion(newX, newY, newZ, newW);
	}

	/**
	 * Normalizes the quaternion.
	 * @return the same quaternion as a unit quaternion
	 */
	public Quaternion normalize() {
		float mag = (float)getMagnitude();
		w 	/= mag;
		v.x /= mag;
		v.y /= mag;
		v.z /= mag;

		return this;
	}

	public double getMagnitude() {
		return Math.sqrt(w * w + v.x * v.x + v.y * v.y + v.z * v.z);
	}

	public float dot(Quaternion other) {
		return dot(this, other);
	}
	public static float dot(Quaternion left, Quaternion right) {
		return left.v.x * right.v.x + left.v.y * right.v.y + left.v.z * right.v.z + left.w * right.w;
	}

	public Quaternion toIdentity() {
		v.x = v.y = v.z = 0;
		w = 1;

		return this;
	}

	public Quaternion invert() {
		v.negate();

		return this;
	}
	public static Quaternion inverted(Quaternion q) {
		return new Quaternion(q.w, q.v.negate(new Vector3f()));
	}

	/**
	 * Converts the quaternion to a 4x4 matrix representing the exact same
	 * rotation as this quaternion. (The rotation is only contained in the
	 * top-left 3x3 part, but a 4x4 matrix is returned here for convenience
	 * seeing as it will be multiplied with other 4x4 matrices).
	 *
	 * @return The rotation matrix which represents the exact same rotation as
	 *         this quaternion.
	 */
	public Matrix4f toRotationMatrix() {
		return toRotationMatrix(new Matrix4f());
	}
	/**
	 * Converts the quaternion to a 4x4 matrix representing the exact same
	 * rotation as this quaternion. (The rotation is only contained in the
	 * top-left 3x3 part, but a 4x4 matrix is returned here for convenience
	 * seeing as it will be multiplied with other 4x4 matrices).
	 *
	 * @return The rotation matrix which represents the exact same rotation as
	 *         this quaternion.
	 */
	public Matrix4f toRotationMatrix(Matrix4f matrix) {
		final float xy = v.x * v.y;
		final float xz = v.x * v.z;
		final float xw = v.x * w;
		final float yz = v.y * v.z;
		final float yw = v.y * w;
		final float zw = v.z * w;
		final float xSquared = v.x * v.x;
		final float ySquared = v.y * v.y;
		final float zSquared = v.z * v.z;
		matrix.m00 = 1 - 2 * (ySquared + zSquared);
		matrix.m01 = 2 * (xy - zw);
		matrix.m02 = 2 * (xz + yw);
		matrix.m03 = 0;
		matrix.m10 = 2 * (xy + zw);
		matrix.m11 = 1 - 2 * (xSquared + zSquared);
		matrix.m12 = 2 * (yz - xw);
		matrix.m13 = 0;
		matrix.m20 = 2 * (xz - yw);
		matrix.m21 = 2 * (yz + xw);
		matrix.m22 = 1 - 2 * (xSquared + ySquared);
		matrix.m23 = 0;
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;
		matrix.m33 = 1;
		return matrix;
	}

	public final void set(Quaternion rotation) {
		w = rotation.w;
		v.set(rotation.v);
	}
	public Quaternion clone() {
		return new Quaternion(w, new Vector3f(v));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Quaternion) {
			return dot((Quaternion)obj) > 0.999999f;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[" + w + ", " + v.x + ", " + v.y + ", " + v.z + "]";
	}
}
