package GLOOP.graphics.rendering;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Map;

public abstract class GBufferShader extends ShaderProgram {
	private static final Vector3f cameraposition = new Vector3f(); // Pass through

	private Uniform1f zfar;
	private Uniform3f campos;
	private Uniform1f time;

	public GBufferShader(String vertexshaderpath, String fragmentshaderpath) throws IOException {
		super(vertexshaderpath, fragmentshaderpath);
	}
	public GBufferShader(String vertexshaderpath, String fragmentshaderpath, Iterable<Map.Entry<String, String>> defines) throws IOException {
		super(vertexshaderpath, fragmentshaderpath, defines);
	}

	@Override
	protected void getCustomUniformLocations() {
		zfar 	= new Uniform1f(this, "zfar");
		campos 	= new Uniform3f(this, "campos");

		time = new Uniform1f(this, "Time");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setzfar(Camera.DEFAULT_ZFAR);
	}

	public void setzfar(float zfar) {
		this.zfar.set(zfar);
	}

	public void setTime(float time) { this.time.set(time); }

	public void setCameraPosition(Vector3f cameraposition) { campos.set(cameraposition); }

	@Override
	public void setCameraUniforms(Camera camera, Matrix4f modelmatrix) {
		super.setCameraUniforms(camera, modelmatrix);

		setzfar(camera.getzfar());
		camera.getPosition(cameraposition);
		setCameraPosition(cameraposition);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
