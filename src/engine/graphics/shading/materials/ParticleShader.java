package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform16f;
import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleShader extends ShaderProgram {
	private Uniform1i Texture;
	private Uniform16f ProjectionMatrix;
	private Uniform16f ViewMatrix;
	private Uniform1f Radius;

	public ParticleShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/Particle/VertexShader.vert",
				"res/shaders/Particle/FragmentShader.frag"
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
		Texture = new Uniform1i(this, "Texture");
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
