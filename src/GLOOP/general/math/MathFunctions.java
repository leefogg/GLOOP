package GLOOP.general.math;

import GLOOP.graphics.data.DataConversion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class MathFunctions {
	private static final Vector3f
		right = new Vector3f(1, 0, 0),
		up = new Vector3f(0, 1, 0),
		in = new Vector3f(0, 0, 1);
	private static final Vector3f passthroughVector = new Vector3f();
	private static final Matrix4f passthoughMatrix = new Matrix4f();

	public static final void createTranslationMatrix(Vector2f position, Matrix4f out) {
		createTranslationMatrix(new Vector3f(position.x, position.y, 0), out);
	}
	public static final void createTranslationMatrix(Vector3f position, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		out.translate(position);
	}

	public static final void createScaleMatrix(Vector2f scale, Matrix4f out) {
		createTranslationMatrix(new Vector3f(scale.x, scale.y, 0), out);
	}
	public static final void createScaleMatrix(Vector3f scale, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		out.scale(scale);
	}

	public static final void createRotatationMatrix(Quaternion rotation, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();

		out.load(rotation.toRotationMatrix(passthoughMatrix));
	}
	public static final void createRotatationMatrix(Vector3f rotation, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		Matrix4f.rotate((float)Math.toRadians(rotation.x), right, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.y), up, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.z), in, out, out);
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
		Matrix4f.mul(out, rotation.toRotationMatrix(passthoughMatrix), out);
		Matrix4f.scale(scale, out, out);
		return out;
	}
	public static Matrix4f createTransformationMatrix(Vector2f translation, Quaternion rotation, Vector3f scale, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		Matrix4f.translate(translation, out, out);
		Matrix4f.mul(out, rotation.toRotationMatrix(passthoughMatrix), out);
		Matrix4f.scale(scale, out, out);
		return out;
	}
	public static Matrix4f createTransformationMatrix(Vector2f translation, Quaternion rotation, Vector2f scale, Matrix4f out) {
		passthroughVector.set(scale.x, scale.y, 1);
		return createTransformationMatrix(translation, rotation, passthroughVector, out);
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

		Matrix4f.rotate((float)Math.toRadians(rotation.x), right, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.y), up, out, out);
		Matrix4f.rotate((float)Math.toRadians(rotation.z), in, out, out);
		position.negate();
		Matrix4f.translate(position, out, out);
		position.negate();

		return out;
	}
	public static Matrix4f createViewMatrix(Vector3f eye, Vector3f target, Vector3f up, Matrix4f out) { // TODO: Optimize object creation
		if (out == null)
			out = new Matrix4f();

		Vector3f
			zaxis = new Vector3f(),
			xaxis = new Vector3f(),
			yaxis = new Vector3f();
		Vector3f.sub(eye, target, zaxis);
		if (zaxis.lengthSquared() != 0f)
			zaxis.normalise();
		Vector3f.cross(up, zaxis, xaxis);
		if (xaxis.lengthSquared() != 0f)
			xaxis.normalise();
		Vector3f.cross(zaxis, xaxis, yaxis);
		if (yaxis.lengthSquared() != 0f)
			yaxis.normalise();
		float
			ex = -Vector3f.dot(xaxis, eye),
			ey = -Vector3f.dot(yaxis, eye),
			ez = -Vector3f.dot(zaxis, eye);
		out.load(DataConversion.toGLBuffer(new float[] {
			xaxis.x,    yaxis.x,   zaxis.x,    0,
			xaxis.y,	yaxis.y,    zaxis.y,   0,
			xaxis.z,    yaxis.z,   	zaxis.z,    0,
			ex,         ey,         ez,        1}
		));
		return out;
	}

	public static void createProjectionMatrix(int width, int height, float fov, float znear, float zfar, Matrix4f out) {
		if (out == null)
			out = new Matrix4f();
		else
			out.setIdentity();

		float aspectRatio = (float)width / (float)height;
		float y_scale = (float)(1f / Math.tan(Math.toRadians(fov / 2f)) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = zfar - znear;

		out.m00 = x_scale;
		out.m11 = y_scale;
		out.m22 = -((zfar + znear) / frustum_length);
		out.m23 = -1f;
		out.m32 = -(2f * znear * zfar / frustum_length);
		out.m33 = 0f;
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

	public static Vector2f GLScreenSpaceToGraphicsSpace(float x, float y, Vector2f out) {
		out.x = x * 2 -1;
		out.y = 1 - y * 2;

		return out;
	}
}