package GLOOP.graphics.rendering.shading.posteffects;

import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.data.models.Model2D;
import GLOOP.graphics.rendering.shading.materials.FullBrightMaterial;
import GLOOP.graphics.rendering.shading.materials.Material;
import GLOOP.graphics.rendering.texturing.Texture;

public class PostProcessor {
	private static final Model2D fullscreenquad = new Model2D(0, 0, Viewport.getWidth(), Viewport.getHeight());
	private static FullBrightMaterial FullBrightMaterial = (FullBrightMaterial)fullscreenquad.getMaterial(); // Model2D's default material is FullBrightMaterial

	public static void render(Texture texture) { render(texture, FullBrightMaterial); }
	public static void render(Texture texture, Material shader) {
		FullBrightMaterial.setAlbedoTexture(texture);
		render(shader);
	}
	public static void render(Texture texture, PostEffect shader) {
		shader.setTexture(texture);
		render(shader);
	}

	// Renders in the currently bound frame buffer
	public static void render(Material material) {
		// Rescale to the current resolution
		fullscreenquad.setScale(Viewport.getWidth(), Viewport.getHeight());

		// rendering post effects shouldn't affect the depth buffer
		Renderer.enableStencilTesting(false);
			Renderer.enableDepthTesting(false);
				Renderer.enableDepthBufferWriting(false);
					fullscreenquad.setMaterial(material);
					fullscreenquad.render();
				Renderer.popDepthBufferWritingState();
			Renderer.popDepthTestingEnabledState();
		Renderer.popStencilTestingState();
	}
}
