package gloop.graphics.rendering.particlesystem;

import gloop.graphics.data.models.DataVolatility;
import gloop.graphics.rendering.texturing.Texture;

import java.io.IOException;

public class DynamicParticleSystem extends ParticleSystem {
	private int lastDead = 0;
	private int lifeTime = 200;
	protected Particle[] particles;

	public DynamicParticleSystem(int numparticles, Texture texture) throws IOException {
		super(numparticles, texture, DataVolatility.Stream);

		initializeParticles(numparticles);
		updateParticlesBuffer();
	}

	protected void initializeParticles(int numparticles) {
		particles = new Particle[numparticles];
		for (int i = 0; i< particles.length; i++)
			particles[i] = new Particle();

		positionsbuffer.store(getPositionsBuffer(particles));
	}

	private void updateParticlesBuffer() {
		updateParticles(particles);
	}

	@Override
	public void update(float delta, float timescaler) {
		for (Particle particle : particles)
			particle.update(delta, timescaler);

		updateParticlesBuffer();
	}

	public Particle getNextDead() {
		int start = lastDead % particles.length;
		for(; lastDead < particles.length; lastDead++)
			if (isParticleDead(particles[lastDead]))
				return particles[lastDead++];
		for(lastDead = 0; lastDead <start; lastDead++)
			if (isParticleDead(particles[lastDead]))
				return particles[lastDead++];

		return null;
	}

	private boolean isParticleDead(Particle particle) {
		return particle.lifetime > lifeTime;
	}

	public void setParticleLifeTime(int frames) { lifeTime = frames; }
	public int getParticleLifeTime() { return lifeTime; }
}
