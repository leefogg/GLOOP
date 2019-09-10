package gloop.graphics.rendering.shading;

import gloop.graphics.cameras.Camera;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import gloop.graphics.rendering.shading.posteffects.PostEffectShader;
import gloop.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Map;

public class GBufferLightingShader extends PostEffectShader {
	private static final Vector3f CAMERAPOSITION = new Vector3f(); // Pass through

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

	public GBufferLightingShader(String vertexshader, String fragmentshader, Iterable<Map.Entry<String, String>> defines) throws ShaderCompilationException, IOException {
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
		setNormalTexture(TextureUnit.GBUFFER_NORMAL);
		setPositionTexture(TextureUnit.GBUFFER_POSITION);
		setSpecularTexture(TextureUnit.GBUFFER_SPECULAR);
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
		camera.getPosition(CAMERAPOSITION);
		setCameraPosition(CAMERAPOSITION);
	}
}
