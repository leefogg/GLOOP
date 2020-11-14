package gloop.graphics.cameras;

import gloop.general.math.MathFunctions;
import org.lwjgl.util.vector.Matrix4f;

public class OrthographicCamera extends Camera {
	private float width, height;
	public OrthographicCamera(int width, int height, float znear, float zfar) {
		super();

		this.znear = znear;
		this.zfar = zfar;
		setSize(width, height);
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;

		projectionMatrix.expire();
	}

	@Override
	protected Matrix4f updateProjectionMatrix(Matrix4f projectionMatrix) {
		return MathFunctions.createOrthoProjectionMatrix(-width /2f, width /2f, -height /2f, height /2f, znear, zfar, projectionMatrix);
	}
}
