package GLOOP.graphics.cameras;

import GLOOP.general.math.MathFunctions;

public class OrthographicCamera extends Camera {
	float width, height;
	public OrthographicCamera(int width, int height, float znear, float zfar) {

		this.znear = znear;
		this.zfar = zfar;
		setSize(width, height);
	}

	public void setSize(float width, float height) {
		this.width = width;
		this.height = height;
		updateProjectionMatrix();
	}

	@Override
	public void setznear(float znear) {
		super.setznear(znear);

		updateProjectionMatrix();
	}

	@Override
	public void setzfar(float zfar) {
		super.setzfar(zfar);

		updateProjectionMatrix();
	}

	private void updateProjectionMatrix() {
		MathFunctions.createOrthoProjectionMatrix(-width /2f, width /2f, -height /2f, height /2f, znear, zfar, ProjectionMatrix);
	}
}
