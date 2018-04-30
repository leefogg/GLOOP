package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public abstract class ParticleEmitter {
	protected static final Random random = new Random();
	protected static final float DefaultEmissionSpeed = 1;
	protected static final Vector3f TempVector = new Vector3f();

	protected DynamicParticleSystem ParticleSystem;

	protected float EmissionSpeed = DefaultEmissionSpeed;

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
