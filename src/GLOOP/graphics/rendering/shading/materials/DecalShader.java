package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.GLSL.CachedUniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class DecalShader extends ShaderProgram {
	private Uniform1i albedoTexture, normalTexture, specularTexture, positionTexture;
	private Uniform3f campos;
	private Uniform16f invModelMatrix;

	public DecalShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/Decal/VertexShader.vert",
				"res/_SYSTEM/Shaders/Decal/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		albedoTexture = new CachedUniform1i(this, "albedomap");
		//normalTexture = new Uniform1i(this, "normalmap");
		specularTexture = new CachedUniform1i(this, "specularmap");
		positionTexture = new CachedUniform1i(this, "positionBuffer");
		campos = new Uniform3f(this, "campos");
		
		invModelMatrix = new Uniform16f(this, "inverseModelMatrix");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		bindTextureUnits();
	}

	public void bindTextureUnits() {
		albedoTexture.set(TextureUnit.AlbedoMap);
		specularTexture.set(TextureUnit.SpecularMap);
		//normalTexture.set(TextureUnit.NormalMap);
		positionTexture.set(TextureUnit.GBuffer_Position);
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}

	public void setCampos(Vector3f campos) { this.campos.set(campos); }

	public void setInverseModelMatrix(Matrix4f matrix) { invModelMatrix.set(matrix); }
}
