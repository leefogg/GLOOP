package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class DepthShader extends ShaderProgram {
	private static Vector3f Temp = new Vector3f();

	private Uniform1f
		znear,
		zfar;
	private Uniform3f campos;

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
		campos = new Uniform3f(this, "campos");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setzfar(30);
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

	public void setCameraPosition(Vector3f position) {
		campos.set(position);
	}

	public void set(Camera camera) {
		setzfar(camera.getzfar());
		setznear(camera.getznear());
		camera.getPosition(Temp);
		setCameraPosition(Temp);
	}
}
