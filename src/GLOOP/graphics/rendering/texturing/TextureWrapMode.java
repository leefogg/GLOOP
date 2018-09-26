package GLOOP.graphics.rendering.texturing;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;
import static org.lwjgl.opengl.GL44.GL_MIRROR_CLAMP_TO_EDGE;

public enum TextureWrapMode {
	EdgeClamp		(GL_CLAMP_TO_EDGE),
	BorderClamp		(GL_CLAMP_TO_BORDER),
	MirroredRepeat  (GL_MIRRORED_REPEAT),
	Repeat			(GL_REPEAT),
	Clamp           (GL_CLAMP),
	MirrorEdgeClamp	(GL_MIRROR_CLAMP_TO_EDGE);

	private final int mode;
	TextureWrapMode(int mode) {
		this.mode = mode;
	}

	public int getGLEnum() {
		return mode;
	}
}
