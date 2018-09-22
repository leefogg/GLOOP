package engine.graphics.shading.materials;

import engine.graphics.cameras.Camera;
import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;

import java.io.IOException;

public final class DepthShader extends ShaderProgram {
	private Uniform1f
		znear,
		zfar;

	public DepthShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/Depth/VertexShader.vert",
			"res/_SYSTEM/Shaders/Depth/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		znear = new Uniform1f(this, "znear");
		zfar = new Uniform1f(this, "zfar");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setzfar(1000);
		setznear(0.01f);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}

	public void setznear(float znear) {
		this.znear.set(znear);
	}

	public void setzfar(float zfar) {
		this.zfar.set(zfar);
	}

	public void set(Camera camera) {
		setzfar(camera.getzfar());
		setznear(camera.getznear());
	}
}
