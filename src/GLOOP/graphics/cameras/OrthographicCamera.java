package GLOOP.graphics.cameras;

import GLOOP.general.math.MathFunctions;

public class OrthographicCamera extends Camera {

	public OrthographicCamera(int width, int height, float znear, float zfar) {
		MathFunctions.createOrthoProjectionMatrix(-width/2f, width/2f, -height/2f, height/2f, znear, zfar, ProjectionMatrix);
		this.znear = znear;
		this.zfar = zfar;
	}
}
