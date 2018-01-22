package engine.graphics.shading.posteffects;

import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.graphics.textures.TextureUnit;

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
