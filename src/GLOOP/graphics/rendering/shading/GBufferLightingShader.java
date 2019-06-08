package GLOOP.graphics.rendering.shading;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.posteffects.PostEffectShader;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class GBufferLightingShader extends PostEffectShader {
	private static final Vector3f cameraposition = new Vector3f(); // Pass through

	private Uniform1i
			positionTexture,
			normalTexture,
			specularTexture;

	private Uniform1f
			znear,
			zfar;

	private Uniform3f campos;

	public GBufferLightingShader(String vertexshader, String fragmentshader) throws ShaderCompilationException, IOException {
		super(vertexshader, fragmentshader);
	}

	public GBufferLightingShader(String vertexshader, String fragmentshader, String[] defines) throws ShaderCompilationException, IOException {
		super(vertexshader, fragmentshader, defines);
	}

	public GBufferLightingShader(VertexShader vertexshader, FragmentShader fragmentshader) {
		super(vertexshader, fragmentshader);
	}

	@Override
	protected void getCustomUniformLocations() {
		// Buffers
		positionTexture = new Uniform1i(this, "positionTexture");
		normalTexture 	= new Uniform1i(this, "normalTexture");
		specularTexture = new Uniform1i(this, "specularTexture");

		// Camera
		znear 	= new Uniform1f(this, "znear");
		zfar 	= new Uniform1f(this, "zfar");
		campos 	= new Uniform3f(this, "campos");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		bindGBuffers();
	}

	public void bindGBuffers() {
		setNormalTexture(TextureUnit.GBuffer_Normal);
		setPositionTexture(TextureUnit.GBuffer_Position);
		setSpecularTexture(TextureUnit.GBuffer_Specular);
	}

	public final void setPositionTexture(int unit) { positionTexture.set(unit); }

	public final void setNormalTexture(int unit) { normalTexture.set(unit); }

	public final void setSpecularTexture(int unit) { specularTexture.set(unit); }

	public void setznear(float znear) { this.znear.set(znear); }

	public void setzfar(float zfar) { this.zfar.set(zfar); }

	public void setCameraPosition(Vector3f cameraposition) { campos.set(cameraposition); }

	public void setCameraAttributes(Camera camera) {
		setzfar(camera.getzfar());
		setznear(camera.getznear());
		camera.getPosition(cameraposition);
		setCameraPosition(cameraposition);
	}
}
