package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform16f;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleShader extends ShaderProgram {


	private Uniform1i texture;
	private Uniform16f ProjectionMatrix;
	private Uniform16f ViewMatrix;

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
		//bindAttribute("color", 2);
	}


	@Override
	protected void getCustomUniformLocations() {
		ProjectionMatrix = new Uniform16f(this, "ProjectionMatrix");
		ViewMatrix = new Uniform16f(this, "ViewMatrix");
		texture = new Uniform1i(this, "Texture");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		texture.set(TextureUnit.AlbedoMap);
	}

	@Override
	public boolean supportsTransparency() {	return true; }

	public void setProjectionMatrix(Matrix4f projectionmatrix) { ProjectionMatrix.set(projectionmatrix); }

	public void setViewMatrix(Matrix4f viewmatrix) { ViewMatrix.set(viewmatrix); }
}
