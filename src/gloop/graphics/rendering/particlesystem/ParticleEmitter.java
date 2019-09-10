package gloop.graphics.rendering.particlesystem;

import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public abstract class ParticleEmitter {
	protected static final Random RANDOM = new Random();
	protected static final float DEFAULT_EMISSION_SPEED = 1;
	protected static final Vector3f TEMP_VECTOR = new Vector3f();

	protected DynamicParticleSystem particleSystem;

	protected float emissionSpeed = DEFAULT_EMISSION_SPEED;

	public ParticleEmitter(DynamicParticleSystem particleSystem) {
		this(particleSystem, 1);
	}
	public ParticleEmitter(DynamicParticleSystem particleSystem, float emissionSpeed) {
		this.particleSystem = particleSystem;
		this.emissionSpeed = emissionSpeed;
	}


	public final void update(float delta, float timescaler) {
		emit((int)(emissionSpeed * timescaler));
	}

	public abstract void emit(int count);


	public float getEmissionSpeed() { return emissionSpeed; }
	public void setEmmisionSpeed(int speed) { emissionSpeed = speed; }
}
