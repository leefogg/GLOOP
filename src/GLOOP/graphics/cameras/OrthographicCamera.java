package GLOOP.graphics.cameras;

import GLOOP.general.math.MathFunctions;

public class OrthographicCamera extends Camera {

	public OrthographicCamera(int width, int height, float znear, float zfar) {
		MathFunctions.createOrthoProjectionMatrix(0, width, 0, height, znear, zfar, ProjectionMatrix);
	}
}
