package engine.graphics.shading.materials;

import engine.graphics.cameras.Camera;
import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.CachedUniform1i;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.GLSL.Uniform3f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
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
		bindAttribute("VertexPosition", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
		bindAttribute("VertexNormal", VertexArray.VertexNormalsIndex);
		bindAttribute("VertexTangent", VertexArray.VertexTangentsIndex);
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
		environmentMap.set(TextureUnit.EnvironmentMap);
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
