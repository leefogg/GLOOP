package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.graphics.rendering.texturing.TextureUnit;

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
		TextureManager.bindTextureToUnit(texture, TextureUnit.ALBEDO_MAP);
	}
}
