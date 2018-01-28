package engine.graphics.shading.posteffects;

import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Viewport;
import engine.graphics.models.Model2D;
import engine.graphics.shading.materials.FullBrightMaterial;
import engine.graphics.shading.materials.Material;
import engine.graphics.textures.Texture;

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
	public static void render(Material material) { //TODO: Change to PostProcess class
		// Rescale to the current resolution
		fullscreenquad.setScale(Viewport.getWidth(), Viewport.getHeight());

		// rendering post effects shouldn't affect the depth buffer
		Renderer.enableDepthTesting(false);
			Renderer.enableDepthBufferWriting(false);
				fullscreenquad.setMaterial(material);
				fullscreenquad.render();
			Renderer.popDepthBufferWritingState();
		Renderer.popDepthTestingEnabledState();
	}
}
