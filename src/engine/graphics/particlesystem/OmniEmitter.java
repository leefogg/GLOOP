package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class OmniEmitter extends ParticleEmitter {
	private Vector3f emitPosition, emitVelocity;
	private Vector3f VelocityError = new Vector3f();

	public OmniEmitter(DynamicParticleSystem system, Vector3f position, Vector3f velocity) {
		super(system);
		this.emitPosition = position;
		this.emitVelocity = velocity;
	}

	@Override
	public void emit(int count) {
		for (int i=0; i<count; i++) {
			Particle nextparticle = ParticleSystem.getNextDead();
			if (nextparticle == null)
				return;

			nextparticle.reset();

			nextparticle.position.set(emitPosition);

			TempVector.set(emitVelocity);
			TempVector.x += (random.nextFloat() * 2 - 1) * VelocityError.x;
			TempVector.y += (random.nextFloat() * 2 - 1) * VelocityError.y;
			TempVector.z += (random.nextFloat() * 2 - 1) * VelocityError.z;
			nextparticle.velocity.set(TempVector);
		}
	}

	public void setEmitVelocityError(Vector3f error) { VelocityError = error; }
}
