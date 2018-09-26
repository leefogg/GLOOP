package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.texturing.FrameBuffer;
import GLOOP.graphics.rendering.texturing.Texture;

public interface PostProcess {
	void render(FrameBuffer target, Texture lastframe);
}
