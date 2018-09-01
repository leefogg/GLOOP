package engine.graphics.shading.materials;

import engine.graphics.cameras.Camera;
import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.GLSL.Uniform3f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class ChromeShader extends ShaderProgram {
	private Uniform3f campos;
	private Uniform1i envmap;

	public ChromeShader() throws ShaderCompilationException, IOException {
		super(
			"res/shaders/chromeshader/VertexShader.vert",
			"res/shaders/chromeshader/FragmentShader.frag"
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
		campos = new Uniform3f(this, "CameraPos");
		envmap = new Uniform1i(this, "environmentMap");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		envmap.set(TextureUnit.EnvironmentMap);
	}

	@Override
	public void setCameraUniforms(Camera camera, Matrix4f modelmatrix) {
		super.setCameraUniforms(camera, modelmatrix);

		setDefaultCustomUniformValues();
	}

	public void setCameraPosition(Vector3f position) { campos.set(position); }

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
