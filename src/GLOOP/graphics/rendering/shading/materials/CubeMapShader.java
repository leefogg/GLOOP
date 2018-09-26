package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public final class CubeMapShader extends ShaderProgram {
	private Uniform1i Texture;
	private Uniform16f ViewMatrix, ProjectionMatrix;

	CubeMapShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Cubemap/VertexShader.vert",
				"res/_SYSTEM/Shaders/Cubemap/FragmentShader.frag"
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
