package engine.graphics.rendering.UI;

import engine.graphics.rendering.Renderer;
import engine.graphics.models.Model2D;

import java.util.ArrayList;

public final class GUIRenderer {
	private static final ArrayList<Model2D> GUIs = new ArrayList<>();

	public static void addGUI(Model2D gui) {
		GUIs.add(gui);
	}

	public static void remove(Model2D gui) {
		GUIs.remove(gui);
	}

	public static void removeAll() {
		GUIs.clear();
	}

	public static void render() {
		render(GUIs);
	}
	public static void render(Iterable<Model2D> UIs) {
		if (!UIs.iterator().hasNext())
			return;

		Renderer.enableBlending();
		Renderer.disableDepthTesting();
		Renderer.disableFaceCulling();

		for (Model2D gui : UIs)
			gui.render();

		Renderer.enableFaceCulling();
		Renderer.enableDepthTesting();
		Renderer.disableBlending();
	}
	public static void render(Model2D[] UIs) {
		if (UIs.length == 0)
			return;

		Renderer.enableBlending();
		Renderer.disableDepthTesting();
		Renderer.disableFaceCulling();

		for (Model2D gui : UIs)
			gui.render();

		Renderer.enableFaceCulling();
		Renderer.enableDepthTesting();
		Renderer.disableBlending();
	}
}
