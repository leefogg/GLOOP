package engine.graphics.rendering;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.PerspectiveCamera;
import engine.graphics.models.Model;
import engine.graphics.shading.lighting.AmbientLight;
import engine.graphics.shading.lighting.DirectionalLight;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.lighting.SpotLight;
import javafx.scene.effect.Light;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

public class Scene {
	private final HashSet<Model> models = new HashSet<>();

	private final ArrayList<PointLight> pointLights = new ArrayList<>();
	private final ArrayList<DirectionalLight> directionalLights = new ArrayList<>();
	private final ArrayList<SpotLight> spotLights = new ArrayList<>();
	private final AmbientLight ambientLight = new AmbientLight();
	public Camera currentCamera = new PerspectiveCamera();

	public final int getNumberOfDirectionalLights() { return directionalLights.size(); }
	public final DirectionalLight getDirectionallight(int index) {
		return directionalLights.get(index);
	}

	public final AmbientLight getAmbientlight() {
		return ambientLight;
	}

	public final int getNumberOfPointLights() { return pointLights.size(); }
	public final PointLight getPointLight(int index) {
		return pointLights.get(index);
	}

	public final int getNumberOfSpotLights() { return spotLights.size(); }
	public final SpotLight getSpotLight(int index) { return spotLights.get(index); }

	public void add(Model model) { models.add(model); }
	public void add(PointLight light) {	pointLights.add(light); }
	public void add(DirectionalLight directionallight) { directionalLights.add(directionallight); }
	public void add(SpotLight spotlight) { spotLights.add(spotlight); }

	public HashSet<Model> getModels() { return models; }

	public ArrayList<Model> selectModels(Predicate<Model> test) {
		ArrayList<Model> selecteditems = new ArrayList<>();
		for (Model item : models) {
			if (test.test(item))
				selecteditems.add(item);
		}

		return selecteditems;
	}
}
