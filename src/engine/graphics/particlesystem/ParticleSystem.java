package engine.graphics.particlesystem;

import engine.Disposable;
import engine.graphics.models.Model2D;
import engine.graphics.models.VertexArray;
import engine.graphics.rendering.Renderer;
import engine.graphics.shading.materials.ParticleMaterial;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.math.MathFunctions;
import engine.math.Quaternion;
import engine.resources.ResourceManager;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

// TODO: Implement Renderable
public class ParticleSystem implements Disposable {
	private static VertexArray QuadGeometry = Model2D.getQuadGeometry();
	private static ParticleMaterial material;
	private static Matrix4f ModelMatrix = new Matrix4f();
	private static Quaternion Rotation = new Quaternion();
	private static Vector3f Scale = new Vector3f(1,1,1);

	private Texture texture;
	private int lastDead = 0;
	private Particle[] particles;
	private int LifeTime = 200;

	public ParticleSystem(int numparticles, Texture texture) throws IOException {
		material = new ParticleMaterial(texture);

		particles = new Particle[numparticles];
		for (int i=0; i<particles.length; i++)
			particles[i] = new Particle();

		this.texture = texture;
	}

	public void update(float delta, float timescaler) {
		for (int i=0; i<particles.length; i++)
			particles[i].update(delta, timescaler);
	}

	public void render() {
		if (QuadGeometry.isDisposed())
			return;

		material.bind();
		material.commit();
		TextureManager.bindAlbedoMap(texture);
		for (int i=0; i<particles.length; i++) {
			Particle particle = particles[i];
			if (particleIsDead(particle))
				continue;

			MathFunctions.createTransformationMatrix(particle.position, Rotation, Scale, ModelMatrix);
			material.setCameraAttributes(Renderer.getRenderer().getScene().currentCamera, ModelMatrix);
			QuadGeometry.render();
		}
	}

	public Particle getNextDead() {
		int start = lastDead % particles.length;
		for(; lastDead<particles.length; lastDead++)
			if (particleIsDead(particles[lastDead]))
				return particles[lastDead];
		for(lastDead = 0; lastDead<start; lastDead++)
			if (particleIsDead(particles[lastDead]))
				return particles[lastDead];

		return null;
	}

	private boolean particleIsDead(Particle particle) {
		return particle.lifetime > LifeTime;
	}

	public void setParticleLifeTime(int frames) { LifeTime = frames; }


	@Override
	public void requestDisposal() {	ResourceManager.queueDisposal(this);	}

	@Override
	public boolean isDisposed() {
		return material.getShader().isDisposed() || QuadGeometry.isDisposed();
	}

	@Override
	public void dispose() {
		material.getShader().dispose();
	}
}
