package gloop.general.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public abstract class MathFunctions {
	private static final Vector3f
		RIGHT = new Vector3f(1, 0, 0),
		UP = new Vector3f(0, 1, 0),
		IN = new Vector3f(0, 0, 1);
	private static final Vector3f
		ZAXIS = new Vector3f(),
		XAXIS = new Vector3f(),
		YAXIS = new Vector3f();
	private static final Vector3f PASSTHROUGH_VECTOR = new Vector3f();
	private static final Matrix4f PASSTHOUGH_MATRIX = new Matrix4f();

	public static void createTranslationMatrix(Vector2f position, Matrix4f out) {
		createTranslationMatrix(new Vector3f(position.x, position.y, 0), out);
	}
	public static void createTranslationMatrix(Vector3f position, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		out.translate(position);
	}

	public static void createScaleMatrix(Vector2f scale, Matrix4f out) {
		createTranslationMatrix(new Vector3f(scale.x, scale.y, 0), out);
	}
	public static void createScaleMatrix(Vector3f scale, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		out.scale(scale);
	}

	public static void createRotatationMatrix(Quaternion rotation, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();

		out.load(rotation.toRotationMatrix(PASSTHOUGH_MATRIX));
	}
	public static void createRotatationMatrix(ReadableVector3f rotation, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		Matrix4f.rotate((float)Math.toRadians(rotation.getX()), RIGHT, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.getY()), UP, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.getZ()), IN, out, out);
	}


	// TODO: Remove new objects in this class
 	public static Matrix4f createTransformationMatrix(Vector3f translation) {
		return createTransformationMatrix(translation, new Vector3f());
	}
	public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f scale) {
		return createTransformationMatrix(translation, new Quaternion(), scale);
	}
	public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternion rotation, Vector3f scale) {
		return createTransformationMatrix(translation, rotation, scale, new Matrix4f());
	}
	public static Matrix4f createTransformationMatrix(Vector3f translation, Quaternion rotation, Vector3f scale, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		Matrix4f.translate(translation, out, out);
		Matrix4f.mul(out, rotation.toRotationMatrix(PASSTHOUGH_MATRIX), out);
		Matrix4f.scale(scale, out, out);
		return out;
	}
	public static Matrix4f createTransformationMatrix(Vector2f translation, Quaternion rotation, Vector3f scale, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		Matrix4f.translate(translation, out, out);
		Matrix4f.mul(out, rotation.toRotationMatrix(PASSTHOUGH_MATRIX), out);
		Matrix4f.scale(scale, out, out);
		return out;
	}
	public static Matrix4f createTransformationMatrix(Vector2f translation, Quaternion rotation, Vector2f scale, Matrix4f out) {
		PASSTHROUGH_VECTOR.set(scale.x, scale.y, 1);
		return createTransformationMatrix(translation, rotation, PASSTHROUGH_VECTOR, out);
	}

	public static Matrix4f createViewMatrix(Vector3f position) {
		return createViewMatrix(position, new Vector3f());
	}
	public static Matrix4f createViewMatrix(Vector3f position, Vector3f rotation) {
		return createViewMatrix(position, rotation, null);
	}
	public static Matrix4f createViewMatrix(Vector3f position, Vector3f rotation, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		Matrix4f.rotate((float)Math.toRadians(rotation.x), RIGHT, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.y), UP, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.z), IN, out, out);
		position.negate();
		Matrix4f.translate(position, out, out);
		position.negate();

		return out;
	}
	public static Matrix4f createViewMatrix(Vector3f eye, Vector3f target, Vector3f up, Matrix4f out) { // TODO: Optimize object creation
		if (out == null)
			out = new Matrix4f();

		Vector3f.sub(eye, target, ZAXIS);
		if (ZAXIS.lengthSquared() != 0f)
			ZAXIS.normalise();
		Vector3f.cross(up, ZAXIS, XAXIS);
		if (XAXIS.lengthSquared() != 0f)
			XAXIS.normalise();
		Vector3f.cross(ZAXIS, XAXIS, YAXIS);
		if (YAXIS.lengthSquared() != 0f)
			YAXIS.normalise();
		float
			ex = -Vector3f.dot(XAXIS, eye),
			ey = -Vector3f.dot(YAXIS, eye),
			ez = -Vector3f.dot(ZAXIS, eye);

		/*
			xaxis.x,    yaxis.x,   zaxis.x,    0,
			xaxis.y,	yaxis.y,    zaxis.y,   0,
			xaxis.z,    yaxis.z,   	zaxis.z,    0,
			ex,         ey,         ez,        1
		*/
		out.m00 = XAXIS.x;
		out.m01 = YAXIS.x;
		out.m02 = ZAXIS.x;
		out.m03 = 0;
		out.m10 = XAXIS.y;
		out.m11 = YAXIS.y;
		out.m12 = ZAXIS.y;
		out.m13 = 0;
		out.m20 = XAXIS.z;
		out.m21 = YAXIS.z;
		out.m22 = ZAXIS.z;
		out.m23 = 0;
		out.m30 = ex;
		out.m31 = ey;
		out.m32 = ez;
		out.m33 = 1;

		return out;
	}

	public static Matrix4f createProjectionMatrix(int width, int height, float fov, float znear, float zfar, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		float aspectRatio = (float)width / (float)height;
		float yscale = (float)(1f / Math.tan(Math.toRadians(fov / 2f)) * aspectRatio);
		float xscale = yscale / aspectRatio;
		float frustumlength = zfar - znear;

		out.m00 = xscale;
		out.m11 = yscale;
		out.m22 = -((zfar + znear) / frustumlength);
		out.m23 = -1f;
		out.m32 = -(2f * znear * zfar / frustumlength);
		out.m33 = 0f;

		return out;
	}

	public static Matrix4f createOrthoProjectionMatrix(float left, float right, float top, float bottom, float near, float far, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		out.m00 = 2.0f / (right - left);
		out.m01 = 0.0f;
		out.m02 = 0.0f;
		out.m03 = 0.0f;

		out.m10 = 0.0f;
		out.m11 = -2.0f / (top - bottom);
		out.m12 = 0.0f;
		out.m13 = 0.0f;

		out.m20 = 0.0f;
		out.m21 = 0.0f;
		out.m22 = -2.0f / (far - near);
		out.m23 = 0.0f;

		out.m30 = -(right + left  ) / (right - left  );
		out.m31 = -(top   + bottom) / (top   - bottom);
		out.m32 = -(far   + near  ) / (far   - near  );
		out.m33 = 1.0f;

		return out;
	}

	public static Vector2f screenSpaceToGraphicsSpace(float x, float y, Vector2f out) {
		out.x = x * 2 -1;
		out.y = 1 - y * 2;

		return out;
	}
}