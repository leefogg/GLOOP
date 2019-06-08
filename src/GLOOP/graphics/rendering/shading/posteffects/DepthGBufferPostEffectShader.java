package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.shading.GBufferLightingShader;

import java.io.IOException;

public class DepthGBufferPostEffectShader extends GBufferLightingShader {

	public DepthGBufferPostEffectShader() throws IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/Depth/VertexShader.vert",
				"res/_SYSTEM/Shaders/PostEffects/Depth/FragmentShader.frag"
		);
	}
}

