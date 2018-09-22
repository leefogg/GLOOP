package engine.graphics.particlesystem;

import engine.graphics.models.DataVolatility;
import engine.graphics.textures.Texture;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class DynamicParticleSystem extends ParticleSystem {
	private int LastDead = 0;
	private int LifeTime = 200;
	protected Particle[] Particles;

	public DynamicParticleSystem(int numparticles, Texture texture) throws IOException {
		super(numparticles, texture, DataVolatility.Stream);

		initializeParticles(numparticles);
		updateParticlesBuffer();
	}

	protected void initializeParticles(int numparticles) {
		Particles = new Particle[numparticles];
		for (int i = 0; i< Particles.length; i++)
			Particles[i] = new Particle();

		positionsbuffer.store(getPositionsBuffer(Particles));
	}

	private void updateParticlesBuffer() {
		updateParticles(Particles);
	}

	@Override
	public void update(float delta, float timescaler) {
		for (Particle particle : Particles)
			particle.update(delta, timescaler);

		updateParticlesBuffer();
	}

	public Particle getNextDead() {
		int start = LastDead % Particles.length;
		for(; LastDead < Particles.length;LastDead++)
			if (particleIsDead(Particles[LastDead]))
				return Particles[LastDead++];
		for(LastDead = 0; LastDead <start; LastDead++)
			if (particleIsDead(Particles[LastDead]))
				return Particles[LastDead++];

		return null;
	}

	private boolean particleIsDead(Particle particle) {
		return particle.lifetime > LifeTime;
	}

	public void setParticleLifeTime(int frames) { LifeTime = frames; }
	public int getParticleLifeTime() { return LifeTime; }
}
