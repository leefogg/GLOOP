package engine.graphics.rendering;

import engine.graphics.cameras.Camera;
import engine.graphics.cameras.PerspectiveCamera;
import engine.graphics.models.Decal;
import engine.graphics.models.Model;
import engine.graphics.models.Model2D;
import engine.graphics.models.Model3D;
import engine.graphics.particlesystem.ParticleEmitter;
import engine.graphics.particlesystem.ParticleSystem;
import engine.graphics.shading.lighting.AmbientLight;
import engine.graphics.shading.lighting.DirectionalLight;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.lighting.SpotLight;
import engine.graphics.textures.EnvironmentProbe;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;

public class Scene {
	private final ArrayList<Model3D> models = new ArrayList<>();
	private final HashSet<Model2D> overlays = new HashSet<>();
	private final HashSet<Decal> decals = new HashSet<>();
	private final ArrayList<PointLight> pointLights = new ArrayList<>();
	private final ArrayList<DirectionalLight> directionalLights = new ArrayList<>();
	private final ArrayList<SpotLight> spotLights = new ArrayList<>();
	private final ArrayList<ParticleSystem> ParticleSystems = new ArrayList<>(2);
	private final ArrayList<ParticleEmitter> ParticleEmitters = new ArrayList<>(5);
	private final ArrayList<EnvironmentProbe> EnvironmentProbes = new ArrayList<>(5);

	private final AmbientLight ambientLight = new AmbientLight();
	private Vector3f fogColor = new Vector3f(0,0,0);
	private float fogDensity = 0.02f;
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

	public int GetNumberOfEnvironmentProbes() { return EnvironmentProbes.size(); }
	public EnvironmentProbe GetEnvironmentProbe(int index) { return EnvironmentProbes.get(index); }

	public void add(Model3D model) { models.add(model); }
	public void add(Model2D model) { overlays.add(model); }
	public void add(Decal decal) { decals.add(decal); }
	public void add(PointLight light) {	pointLights.add(light); }
	public void add(DirectionalLight directionallight) { directionalLights.add(directionallight); }
	public void add(SpotLight spotlight) { spotLights.add(spotlight); }
	public void add(ParticleSystem particlesystem) { ParticleSystems.add(particlesystem); }
	public void add(ParticleEmitter emitter) { ParticleEmitters.add(emitter); }
	public void add(EnvironmentProbe probe) { EnvironmentProbes.add(probe); }

	// TODO: These should return readonly
	public ArrayList<Model3D> getModels() { return models; }
	public HashSet<Model2D> getOverlays() { return overlays; }
	public HashSet<Decal> getDecals() { return decals; }
	public List<ParticleSystem> getParticleSystems() { return ParticleSystems; }

	//TODO: Move this to some animation thread
	public void update(float delta, float timescaler) {
		for (ParticleSystem ps  : ParticleSystems)
			ps.update(delta, timescaler);
		for (ParticleEmitter emitter : ParticleEmitters)
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
