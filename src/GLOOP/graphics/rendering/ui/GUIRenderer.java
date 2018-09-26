package GLOOP.graphics.rendering.ui;

import GLOOP.graphics.data.models.Model2D;
import GLOOP.graphics.rendering.Renderer;

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

		Renderer.enableBlending(true);
		Renderer.enableDepthTesting(false);
		Renderer.enableFaceCulling(false);

		for (Model2D gui : UIs)
			gui.render();

		Renderer.popFaceCullingEnabledState();
		Renderer.popDepthTestingEnabledState();
		Renderer.popBlendingEnabledState();
	}
	public static void render(Model2D[] UIs) { // TODO: Find a way to make this DRY
		if (UIs.length == 0)
			return;

		Renderer.enableBlending(true);
		Renderer.enableDepthTesting(false);
		Renderer.enableFaceCulling(false);

		for (Model2D gui : UIs)
			gui.render();

		Renderer.popFaceCullingEnabledState();
		Renderer.popDepthTestingEnabledState();
		Renderer.popBlendingEnabledState();
	}
}
