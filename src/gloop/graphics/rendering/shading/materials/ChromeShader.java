package gloop.graphics.rendering.shading.materials;

import gloop.graphics.cameras.Camera;
import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class ChromeShader extends ShaderProgram {
	private Uniform3f cameraPosition, environmentMapPosition, environmentMapSize;
	private Uniform1i environmentMap;

	public ChromeShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/Chrome/VertexShader.vert",
			"res/_SYSTEM/Shaders/Chrome/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("VertexPosition", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
		bindAttribute("VertexNormal", VertexArray.VERTEX_NORMALS_INDEX);
		bindAttribute("VertexTangent", VertexArray.VERTEX_TANGENTS_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		cameraPosition = new Uniform3f(this, "cameraPos");
		environmentMap = new CachedUniform1i(this, "environmentMap");
		environmentMapPosition = new Uniform3f(this, "envMapPos");
		environmentMapSize = new Uniform3f(this, "envMapSize");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		environmentMap.set(TextureUnit.ENVIRONMENT_MAP);
	}

	@Override
	public void setCameraUniforms(Camera camera, Matrix4f modelmatrix) {
		super.setCameraUniforms(camera, modelmatrix);

		setDefaultCustomUniformValues();
	}

	public void setCameraPosition(Vector3f position) { cameraPosition.set(position); }

	public void setEnvironmentMapPosition(Vector3f position) { environmentMapPosition.set(position); }
	public void setEnvironmentMapSize(Vector3f size) { environmentMapSize.set(size); }

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
