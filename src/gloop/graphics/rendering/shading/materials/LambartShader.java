package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class LambartShader extends ShaderProgram {
	private Uniform3f
		lightPosition,
		lightColor;
	private Uniform1f lightQuadraticAttenuation;
	private Uniform1i texture;

	public LambartShader() throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/Lambart/VertexShader.vert",
			"res/_SYSTEM/Shaders/Lambart/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
		bindAttribute("TextureCoords", VertexArray.TEXTURE_COORDINATES_INDEX);
		bindAttribute("VertexNormal", VertexArray.VERTEX_NORMALS_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		texture = new Uniform1i(this, "Texture");

		lightColor = new Uniform3f(this, "LightColor");
		lightPosition = new Uniform3f(this, "LightPosition");
		lightQuadraticAttenuation = new Uniform1f(this, "LightQuadraticAttenuation");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		texture.set(TextureUnit.ALBEDO_MAP);
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
