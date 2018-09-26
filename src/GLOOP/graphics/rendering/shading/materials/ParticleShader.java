package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleShader extends ShaderProgram {
	private Uniform1i Texture;
	private Uniform16f ProjectionMatrix;
	private Uniform16f ViewMatrix;
	private Uniform1f Radius;

	public ParticleShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Particle/VertexShader.vert",
				"res/_SYSTEM/Shaders/Particle/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
	}


	@Override
	protected void getCustomUniformLocations() {
		ProjectionMatrix = new Uniform16f(this, "ProjectionMatrix");
		ViewMatrix = new Uniform16f(this, "ViewMatrix");
		Texture = new CachedUniform1i(this, "Texture");
		Radius = new Uniform1f(this, "Radius");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		Texture.set(TextureUnit.AlbedoMap);
	}

	@Override
	public boolean supportsTransparency() {	return true; }

	public void setProjectionMatrix(Matrix4f projectionmatrix) { ProjectionMatrix.set(projectionmatrix); }

	public void setViewMatrix(Matrix4f viewmatrix) { ViewMatrix.set(viewmatrix); }

	public void setRadius(float radius) { Radius.set(radius); }
}
