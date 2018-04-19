package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class OmniEmitter {
	private static Random random = new Random();

	private Vector3f emitPosition, emitVelocity;
	private ParticleSystem system;
	private Vector3f randomVelocity = new Vector3f();

	public OmniEmitter(ParticleSystem system, Vector3f position, Vector3f velocity) {
		this.system = system;
		this.emitPosition = position;
		this.emitVelocity = velocity;
	}

	public void update() {
		Particle nextparticle = system.getNextDead();
		if (nextparticle != null) {
			nextparticle.reset();
			nextparticle.position = new Vector3f(emitPosition);

			Vector3f vel = new Vector3f(emitVelocity);
			vel.x += (random.nextFloat()*2-1) * randomVelocity.x;
			vel.y += (random.nextFloat()*2-1) * randomVelocity.y;
			vel.z +=( random.nextFloat()*2-1) * randomVelocity.z;
			nextparticle.velocity = vel;
		}
	}

	public void setEmitVelocityError(Vector3f error) {
		randomVelocity = error;
	}
}
