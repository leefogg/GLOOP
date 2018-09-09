package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.CachedUniform1i;
import engine.graphics.shading.GLSL.Uniform16f;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public final class CubeMapShader extends ShaderProgram {
	private Uniform1i Texture;
	private Uniform16f ViewMatrix, ProjectionMatrix;

	CubeMapShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/cubemap/VertexShader.vert",
				"res/shaders/cubemap/FragmentShader.frag"
			);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		Texture = new CachedUniform1i(this, "cubeMap");
		ViewMatrix = new Uniform16f(this, "ViewMatrix");
		ProjectionMatrix = new Uniform16f(this, "ProjectionMatrix");
	}

	public void setViewMatrix(Matrix4f viewmatrix) { ViewMatrix.set(viewmatrix); }
	public void setProjectionMatrix(Matrix4f projectionmatrix) { ProjectionMatrix.set(projectionmatrix); }

	@Override
	protected void setDefaultCustomUniformValues() {
		Texture.set(TextureUnit.EnvironmentMap);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
