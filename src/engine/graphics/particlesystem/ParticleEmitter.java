package engine.graphics.particlesystem;

import java.util.Random;

public abstract class ParticleEmitter {
	protected static final Random random = new Random();

	protected DynamicParticleSystem ParticleSystem;

	protected float EmissionSpeed = 1;

	public ParticleEmitter(DynamicParticleSystem particleSystem) {
		this(particleSystem, 1);
	}
	public ParticleEmitter(DynamicParticleSystem particleSystem, float emissionSpeed) {
		ParticleSystem = particleSystem;
		EmissionSpeed = emissionSpeed;
	}


	public final void update(float delta, float timescaler) {
		emit((int)(EmissionSpeed * timescaler));
	}

	public abstract void emit(int count);


	public float getEmissionSpeed() { return EmissionSpeed; }
	public void setEmmisionSpeed(int speed) { EmissionSpeed = speed; }
}
