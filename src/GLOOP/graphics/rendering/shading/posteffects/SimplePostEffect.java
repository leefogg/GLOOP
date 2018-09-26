package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import GLOOP.graphics.rendering.texturing.TextureUnit;

public final class SimplePostEffect extends PostEffect<PostEffectShader> {
	protected PostEffectShader shader;

	public SimplePostEffect(PostEffectShader shader) {
		this.shader = shader;
	}

	@Override
	public PostEffectShader getShader() {
		return shader;
	}

	@Override
	public void commit() { }

	@Override
	public void setTexture(Texture texture) {
		TextureManager.bindTextureToUnit(texture, TextureUnit.AlbedoMap);
	}
}
