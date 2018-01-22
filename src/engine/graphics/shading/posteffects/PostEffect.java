package engine.graphics.shading.posteffects;

import engine.graphics.shading.materials.Material;
import engine.graphics.textures.Texture;

public abstract class PostEffect<T extends  PostEffectShader> extends Material<T> {

	public abstract void setTexture(Texture texture);

	@Override
	protected boolean hasTransparency() { return false; }
}
