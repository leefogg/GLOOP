package gloop.graphics.rendering;

import gloop.graphics.cameras.Camera;
import gloop.graphics.cameras.PerspectiveCamera;
import gloop.graphics.data.models.Decal;
import gloop.graphics.data.models.Model;
import gloop.graphics.data.models.Model2D;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.rendering.particlesystem.ParticleEmitter;
import gloop.graphics.rendering.particlesystem.ParticleSystem;
import gloop.graphics.rendering.shading.lights.AmbientLight;
import gloop.graphics.rendering.shading.lights.DirectionalLight;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.lights.SpotLight;
import gloop.graphics.rendering.texturing.EnvironmentProbe;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class Scene {
	private final ArrayList<Model3D> models = new ArrayList<>();
	private final HashSet<Model2D> overlays = new HashSet<>();
	private final HashSet<Decal> decals = new HashSet<>();
	private final List<PointLight> pointLights = new ArrayList<>();
	private final List<DirectionalLight> directionalLights = new ArrayList<>();
	private final List<SpotLight> spotLights = new ArrayList<>();
	private final List<ParticleSystem> particleSystems = new ArrayList<>(2);
	private final List<ParticleEmitter> particleemitters = new ArrayList<>(5);
	private final List<EnvironmentProbe> environmentprobes = new ArrayList<>(5);

	private final AmbientLight ambientLight = new AmbientLight();
	private final ReadableVector3f fogColor = new Vector3f(0,0,0);
	private final float fogDensity = 0.02f;
	private Camera gameCamera = new PerspectiveCamera();
	private Camera debugCamera = gameCamera;

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

	public void  getFogColor(Vector3f passthrough) { passthrough.set(fogColor); }
	public float getFogDensity() { return fogDensity; }

	public Camera getGameCamera() {	return gameCamera; }
	public void setGameCamera(Camera gameCamera) { this.gameCamera = gameCamera; }
	public Camera getDebugCamera() { return debugCamera; }
	public void setDebugCamera(Camera debugCamera) { this.debugCamera = debugCamera; }

	public int getNumberOfEnvironmentProbes() { return environmentprobes.size(); }
	public EnvironmentProbe getEnvironmentProbe(int index) { return environmentprobes.get(index); }

	public void add(Model3D model) { models.add(model); }
	public void add(Model2D model) { overlays.add(model); }
	public void add(Decal decal) { decals.add(decal); }
	public void add(PointLight light) {	pointLights.add(light); }
	public void add(DirectionalLight directionallight) { directionalLights.add(directionallight); }
	public void add(SpotLight spotlight) { spotLights.add(spotlight); }
	public void add(ParticleSystem particlesystem) { particleSystems.add(particlesystem); }
	public void add(ParticleEmitter emitter) { particleemitters.add(emitter); }
	public void add(EnvironmentProbe probe) { environmentprobes.add(probe); }

	// TODO: These should return readonly
	public ArrayList<Model3D> getModels() { return models; }
	public HashSet<Model2D> getOverlays() { return overlays; }
	public HashSet<Decal> getDecals() { return decals; }
	public List<ParticleSystem> getParticleSystems() { return particleSystems; }

	//TODO: Move this to some animation thread
	public void update(float delta, float timescaler) {
		for (ParticleSystem ps  : particleSystems)
			ps.update(delta, timescaler);
		for (ParticleEmitter emitter : particleemitters)
			emitter.update(delta, timescaler);
	}

	// TODO: Use this
	public ArrayList<Model> selectModels(Predicate<Model> test) {
		ArrayList<Model> selecteditems = new ArrayList<>();
		for (Model item : models) {
			if (test.test(item))
				selecteditems.add(item);
		}

		return selecteditems;
	}
}
