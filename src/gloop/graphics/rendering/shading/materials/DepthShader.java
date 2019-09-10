package gloop.graphics.rendering.shading.materials;

import gloop.graphics.cameras.Camera;
import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Map;

public final class DepthShader extends ShaderProgram {
	private static final Vector3f TEMP = new Vector3f();

	private Uniform1f
		znear,
		zfar;
	private Uniform3f campos;
	private Uniform1i albedoMap;

	public DepthShader(Iterable<Map.Entry<String, String>> defines) throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/Depth/VertexShader.vert",
			"res/_SYSTEM/Shaders/Depth/FragmentShader.frag",
				defines
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		znear = new Uniform1f(this, "znear");
		zfar = new Uniform1f(this, "zfar");
		campos = new Uniform3f(this, "campos");
		albedoMap = new CachedUniform1i(this, "albedoMap");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setzfar(30);
		setznear(0.01f);
		albedoMap.set(TextureUnit.ALBEDO_MAP);
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
		camera.getPosition(TEMP);
		setCameraPosition(TEMP);
	}

	@Override
	public void setCameraUniforms(Camera camera, Matrix4f modelmatrix) {
		set(camera);

		super.setCameraUniforms(camera, modelmatrix);
	}
}
