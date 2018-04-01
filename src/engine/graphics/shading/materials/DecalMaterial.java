package engine.graphics.shading.materials;

import engine.graphics.cameras.Camera;
import engine.graphics.data.DataConversion;
import engine.graphics.rendering.DeferredRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.textures.FrameBufferManager;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.*;

public class DecalMaterial extends Material<DecalShader> {
	private static final int[] EnabledAttachments = new int[]{
			GL_COLOR_ATTACHMENT0,
			GL_COLOR_ATTACHMENT1
	};

	private static DecalShader shader;

	private Texture albedoMap, specularMap, normalMap;
	private static Vector3f campos = new Vector3f();

	public DecalMaterial(Texture albedo) throws IOException {
		shader = getDefultShaderSingleton();
		setAlbedoTexture(albedo);
	}

	@Override
	public DecalShader getShader() {
		return shader;
	}

	public static final DecalShader getDefultShaderSingleton() throws IOException {
		if (shader == null)
			shader = new DecalShader();

		return shader;
	}

	@Override
	public void commit() {
		TextureManager.bindAlbedoMap(albedoMap);
		if (specularMap != null)
			TextureManager.bindSpecularMap(specularMap);
		//if (normalMap != null)
		//  TextureManager.bindNormalMap(normalMap);
		shader.bindTextureUnits();

		FrameBufferManager.getCurrentFrameBuffer().bindRenderAttachments(EnabledAttachments);
	}

	@Override
	public void setCameraAttributes(Camera currentcamera, Matrix4f modelmatrix) {
		shader.setCameraUniforms(currentcamera, modelmatrix);

		currentcamera.getPosition(campos);
		shader.setCampos(campos);

		modelmatrix.invert();
		shader.setInverseModelMatrix(modelmatrix);
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
	public boolean useDeferredPipeline() { return true; }

	@Override
	protected boolean hasTransparency() {
		return false;
	}
}