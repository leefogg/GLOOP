package gloop.graphics.rendering.shading.materials;

import gloop.graphics.cameras.Camera;
import gloop.graphics.rendering.texturing.FrameBufferManager;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class DecalMaterial extends Material<DecalShader> {
	private static final int[] ENABLED_ATTACHMENTS = new int[]{
		GL_COLOR_ATTACHMENT0,
		GL_COLOR_ATTACHMENT1
	};
	private static DecalShader Shader;
	private static final Vector3f CAMPOS = new Vector3f();

	private Texture albedoMap, specularMap, normalMap;

	public DecalMaterial(Texture albedo) throws IOException {
		Shader = getDefaultShaderSingleton();
		setAlbedoTexture(albedo);
	}

	@Override
	public DecalShader getShader() {
		return Shader;
	}

	private static DecalShader getDefaultShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new DecalShader();

		return Shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(albedoMap);
		if (specularMap != null)
			TextureManager.bindSpecularMap(specularMap);
		//if (normalMap != null)
		//  TextureManager.bindNormalMap(normalMap);
		Shader.bindTextureUnits();

		FrameBufferManager.getCurrentFrameBuffer().enableRenderAttachments(ENABLED_ATTACHMENTS);
	}

	@Override
	public void setCameraAttributes(Camera currentcamera, Matrix4f modelmatrix) {
		Shader.setCameraUniforms(currentcamera, modelmatrix);

		currentcamera.getPosition(CAMPOS);
		Shader.setCampos(CAMPOS);

		modelmatrix.invert();
		Shader.setInverseModelMatrix(modelmatrix);
	}

	public void setAlbedoTexture(Texture texture) {
		albedoMap = texture;
	}
	public void setSpecularTexture(Texture texture) {
		specularMap = texture;
	}
	public void setNormalTexture(Texture texture) {
		normalMap = texture;
	}


	@Override
	public boolean usesDeferredPipeline() { return true; }

	@Override
	protected boolean hasTransparency() {
		return false;
	}

	@Override
	public boolean supportsShadowMaps() { return false; }
}
