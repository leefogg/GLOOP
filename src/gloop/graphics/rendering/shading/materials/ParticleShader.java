package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.CachedUniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform16f;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class ParticleShader extends ShaderProgram {
	private Uniform1i texture;
	private Uniform16f projectionMatrix;
	private Uniform16f viewMatrix;
	private Uniform1f radius;

	public ParticleShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Particle/VertexShader.vert",
				"res/_SYSTEM/Shaders/Particle/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
	}


	@Override
	protected void getCustomUniformLocations() {
		projectionMatrix = new Uniform16f(this, "ProjectionMatrix");
		viewMatrix = new Uniform16f(this, "ViewMatrix");
		texture = new CachedUniform1i(this, "Texture");
		radius = new Uniform1f(this, "Radius");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		texture.set(TextureUnit.ALBEDO_MAP);
	}

	@Override
	public boolean supportsTransparency() {	return true; }

	public void setProjectionMatrix(Matrix4f projectionmatrix) { projectionMatrix.set(projectionmatrix); }

	public void setViewMatrix(Matrix4f viewmatrix) { viewMatrix.set(viewmatrix); }

	public void setRadius(float radius) { this.radius.set(radius); }
}
