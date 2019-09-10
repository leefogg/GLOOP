package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.texturing.FrameBuffer;
import gloop.graphics.rendering.texturing.Texture;

public interface PostProcess {
	void render(FrameBuffer target, Texture lastframe);
}
