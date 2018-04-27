package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class OmniEmitter extends ParticleEmitter {
	private Vector3f emitPosition, emitVelocity;
	private Vector3f randomVelocity = new Vector3f();

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
			nextparticle.position = new Vector3f(emitPosition);

			Vector3f vel = new Vector3f(emitVelocity);
			vel.x += (random.nextFloat() * 2 - 1) * randomVelocity.x;
			vel.y += (random.nextFloat() * 2 - 1) * randomVelocity.y;
			vel.z += (random.nextFloat() * 2 - 1) * randomVelocity.z;
			nextparticle.velocity = vel;
		}
	}

	public void setEmitVelocityError(Vector3f error) { randomVelocity = error; }
}
