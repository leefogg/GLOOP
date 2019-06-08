package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.shading.GBufferLightingShader;
import GLOOP.graphics.rendering.texturing.FrameBuffer;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;

public abstract class GBufferPostEffect<T extends GBufferLightingShader> extends PostEffect<T> {
	private Texture
			positionTexture,
			normalTexture,
			specularTexture;

	protected T shader;

	public GBufferPostEffect(T shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		this(shader);

		setNormalTexture(normalbuffer);
		setSpecularTexture(specularbuffer);
		setPositionTexture(positionbuffer);
	}
	public GBufferPostEffect(T shader) {
		this.shader = shader;
	}

	@Override
	public T getShader() {
		return shader;
	}

	@Override
	public void bind() {
		super.bind();
		shader.bindGBuffers();
	}

	public final void setPositionTexture(Texture positiontexture) {
		this.positionTexture = positiontexture;
	}
	public final void setNormalTexture(Texture normaltexture) {
		this.normalTexture = normaltexture;
	}
	public final void setSpecularTexture(Texture speculartexture) {
		this.specularTexture = speculartexture;
	}

	@Override
	public void commit() {
		// Bind GBuffer texturing to the uniforms
		TextureManager.bindTextureToUnit(normalTexture, TextureUnit.GBuffer_Normal);
		TextureManager.bindTextureToUnit(positionTexture, TextureUnit.GBuffer_Position);
		TextureManager.bindTextureToUnit(specularTexture, TextureUnit.GBuffer_Specular);
		shader.setCameraAttributes(Renderer.getCurrentCamera());
	}

	@Override
	public void setTexture(Texture texture) {
		// Not used in the post processor
	}

	//TODO: Horrible hack, find a nicer way that eliminates the second method below
	public void render() {
		render(null, null);
	}
	@Override
	public void render(FrameBuffer target, Texture lastframe) {
		PostProcessor.render(this);
	}
}
