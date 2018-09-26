package GLOOP.graphics.rendering;

import GLOOP.graphics.rendering.shading.posteffects.PostEffect;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;

final class LightingPassPostEffect extends PostEffect<LightingPassShader> {
	private Texture
		positionTexture,
		normalTexture,
		specularTexture;
	private float volumetricLightsStrength = 2;

	private static LightingPassShader shader;

	public LightingPassPostEffect(LightingPassShader shader, Texture normalbuffer, Texture specularbuffer, Texture positionbuffer) {
		this.shader = shader;

		setNormalTexture(normalbuffer);
		setSpecularTexture(specularbuffer);
		setPositionTexture(positionbuffer);
	}

	@Override
	public LightingPassShader getShader() {
		return shader;
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
	public void bind() {
		super.bind();
		shader.bindGBuffers();
	}

	@Override
	public void commit() {
		// Bind GBuffer texturing to the uniforms
		TextureManager.bindTextureToUnit(normalTexture, TextureUnit.GBuffer_Normal);
		TextureManager.bindTextureToUnit(positionTexture, TextureUnit.GBuffer_Position);
		TextureManager.bindTextureToUnit(specularTexture, TextureUnit.GBuffer_Specular);
		shader.setCameraAttributes(Renderer.getCurrentCamera());
		shader.updateLights();
		shader.setVolumetricLightsStrength(volumetricLightsStrength);
		shader.setTime(Viewport.getElapsedSeconds());
	}

	public void setVolumetricLightsStrength(float volumetriclightsstrength) { volumetricLightsStrength = volumetriclightsstrength; }

	@Override
	public void setTexture(Texture texture) {
		// Not used in the post processor
	}
}
