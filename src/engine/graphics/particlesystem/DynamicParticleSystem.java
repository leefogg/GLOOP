package engine.graphics.particlesystem;

import engine.graphics.data.DataConversion;
import engine.graphics.models.DataVolatility;
import engine.graphics.textures.Texture;

import java.io.IOException;

// TODO: Implement Renderable
public class DynamicParticleSystem extends ParticleSystem {
	private int lastDead = 0;
	private int LifeTime = 200;
	protected Particle[] particles;

	public DynamicParticleSystem(int numparticles, Texture texture) throws IOException {
		super(numparticles, texture, DataVolatility.Stream);

		initializeParticles(numparticles);
		updateParticlesBuffer();
	}

	protected void initializeParticles(int numparticles) {
		particles = new Particle[numparticles];
		for (int i=0; i<particles.length; i++)
			particles[i] = new Particle();

		positionsbuffer.store(DataConversion.toGLBuffer(getPositionsBuffer(particles)));
	}

	private void updateParticlesBuffer() {
		updateParticles(particles);
	}

	@Override
	public void update(float delta, float timescaler) {
		for (int i=0; i<particles.length; i++)
			particles[i].update(delta, timescaler);

		updateParticlesBuffer();
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
}
