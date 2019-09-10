package gloop.graphics.rendering.ui;

import gloop.graphics.data.models.Model2D;
import gloop.graphics.rendering.Renderer;

import java.util.ArrayList;
import java.util.List;

public final class GUIRenderer {
	private static final List<Model2D> GUIS = new ArrayList<>();

	public static void addGUI(Model2D gui) {
		GUIS.add(gui);
	}

	public static void remove(Model2D gui) {
		GUIS.remove(gui);
	}

	public static void removeAll() {
		GUIS.clear();
	}

	public static void render() {
		render(GUIS);
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
