package engine.graphics.shading.posteffects;

import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.Texture;

public interface PostProcess {
	void render(FrameBuffer target, Texture lastframe);
}
