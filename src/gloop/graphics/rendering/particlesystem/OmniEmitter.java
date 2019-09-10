package gloop.graphics.rendering.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class OmniEmitter extends ParticleEmitter {
	private final Vector3f emitPosition;
	private final Vector3f emitVelocity;
	private Vector3f velocityError = new Vector3f();

	public OmniEmitter(DynamicParticleSystem system, Vector3f position, Vector3f velocity) {
		super(system);
		this.emitPosition = position;
		this.emitVelocity = velocity;
	}

	@Override
	public void emit(int count) {
		for (int i=0; i<count; i++) {
			Particle nextparticle = particleSystem.getNextDead();
			if (nextparticle == null)
				return;

			nextparticle.reset();

			nextparticle.position.set(emitPosition);

			TEMP_VECTOR.set(emitVelocity);
			TEMP_VECTOR.x += (RANDOM.nextFloat() * 2 - 1) * velocityError.x;
			TEMP_VECTOR.y += (RANDOM.nextFloat() * 2 - 1) * velocityError.y;
			TEMP_VECTOR.z += (RANDOM.nextFloat() * 2 - 1) * velocityError.z;
			nextparticle.velocity.set(TEMP_VECTOR);
		}
	}

	public void setEmitVelocityError(Vector3f error) { velocityError = error; }
}
