package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.GLSL.Uniform3f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class LambartShader extends ShaderProgram {
	private Uniform3f
		lightPosition,
		lightColor;
	private Uniform1f lightQuadraticAttenuation;
	private Uniform1i Texture;

	public LambartShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/Lambart/VertexShader.vert",
			"res/_SYSTEM/Shaders/Lambart/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
		bindAttribute("VertexNormal", VertexArray.VertexNormalsIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		Texture = new Uniform1i(this, "Texture");

		lightColor = new Uniform3f(this, "LightColor");
		lightPosition = new Uniform3f(this, "LightPosition");
		lightQuadraticAttenuation = new Uniform1f(this, "LightQuadraticAttenuation");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		Texture.set(TextureUnit.AlbedoMap);
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}

	public void setLightPosition(Vector3f position) {
		setLightPosition(position.x, position.y, position.z);
	}
	public void setLightPosition(float x, float y, float z) {
		lightPosition.set(x, y, z);
	}

	public void setLightColor(float r, float g, float b) {
		lightColor.set(r, g, b);
	}

	public void setLightquadraticAttenuation(float quadraticAttenuation) {
		lightQuadraticAttenuation.set(quadraticAttenuation);
	}
}
