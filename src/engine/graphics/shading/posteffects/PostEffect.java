package engine.graphics.shading.posteffects;

import engine.graphics.shading.materials.Material;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.Texture;

public abstract class PostEffect<T extends PostEffectShader> extends Material<T> implements PostProcess {
	private boolean enabled = true;

	public abstract void setTexture(Texture texture);

	@Override
	protected boolean hasTransparency() { return false; }


	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	public void render(FrameBuffer target, Texture lastframe) {
		target.bind();
		PostProcessor.render(lastframe, this);
	}
}
