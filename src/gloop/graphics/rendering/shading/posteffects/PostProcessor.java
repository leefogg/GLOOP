package gloop.graphics.rendering.shading.posteffects;

import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Viewport;
import gloop.graphics.data.models.Model2D;
import gloop.graphics.rendering.shading.materials.FullBrightMaterial;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.texturing.Texture;

public abstract class PostProcessor {
	private static final Model2D FULLSCREENQUAD = new Model2D(0, 0, Viewport.getWidth(), Viewport.getHeight());
	private static final FullBrightMaterial FULL_BRIGHT_MATERIAL = (FullBrightMaterial) FULLSCREENQUAD.getMaterial(); // Model2D's default material is FullBrightMaterial
	private static boolean PostModeEnabled = false;

	public static void render(Texture texture) { render(texture, FULL_BRIGHT_MATERIAL); }
	public static void render(Texture texture, Material shader) {
		FULL_BRIGHT_MATERIAL.setAlbedoTexture(texture);
		render(shader);
	}
	public static void render(Texture texture, PostEffect shader) {
		shader.setTexture(texture);
		render(shader);
	}
	public static void render(Material material) {
		// Rescale to the current resolution
		FULLSCREENQUAD.setScale(Viewport.getWidth(), Viewport.getHeight());

		beginPostEffects();
		FULLSCREENQUAD.setMaterial(material);
		FULLSCREENQUAD.render();
		endPostEffects();
	}

	public static void beginPostEffects() {
		if (PostModeEnabled)
			return;

		// rendering post effects shouldn't affect the depth buffer
		Renderer.enableStencilTesting(false);
		Renderer.enableDepthTesting(false);
		Renderer.enableDepthBufferWriting(false);

		PostModeEnabled = true;
	}

	public static void endPostEffects() {
		if (!PostModeEnabled)
			return;

		Renderer.popDepthBufferWritingState();
		Renderer.popDepthTestingEnabledState();
		Renderer.popStencilTestingState();

		PostModeEnabled = false;
	}
}
