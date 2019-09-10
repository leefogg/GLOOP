package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform16f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public final class CubeMapShader extends ShaderProgram {
	private Uniform1i texture;
	private Uniform16f viewMatrix, projectionMatrix;

	CubeMapShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Cubemap/VertexShader.vert",
				"res/_SYSTEM/Shaders/Cubemap/FragmentShader.frag"
			);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new CachedUniform1i(this, "cubeMap");
		viewMatrix = new Uniform16f(this, "ViewMatrix");
		projectionMatrix = new Uniform16f(this, "ProjectionMatrix");
	}

	public void setViewMatrix(Matrix4f viewmatrix) { viewMatrix.set(viewmatrix); }
	public void setProjectionMatrix(Matrix4f projectionmatrix) { projectionMatrix.set(projectionmatrix); }

	@Override
	protected void setDefaultCustomUniformValues() {
		texture.set(TextureUnit.ENVIRONMENT_MAP);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}
}
