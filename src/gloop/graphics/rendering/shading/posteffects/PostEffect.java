package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.texturing.FrameBuffer;
import gloop.graphics.rendering.texturing.Texture;

public abstract class PostEffect<T extends PostEffectShader> extends Material<T> implements PostProcess {
	private boolean enabled = true;

	public abstract void setTexture(Texture texture);

	@Override
	protected boolean hasTransparency() { return false; }


	public boolean isEnabled() { return enabled; }
	public void setEnabled(boolean enabled) { this.enabled = enabled; }

	@Override
	public boolean supportsShadowMaps() {
		return false;
	}

	public void render(FrameBuffer target, Texture lastframe) {
		target.bind();
		PostProcessor.render(lastframe, this);
	}
}
