package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class RectangleEmitter extends ParticleEmitter {
	private Vector3f
			Position,
			Size,
			newparticleposition = new Vector3f();

	public RectangleEmitter(DynamicParticleSystem particleSystem, Vector3f position, Vector3f size) {
		this(particleSystem, DefaultEmissionSpeed, position, size);
	}
	public RectangleEmitter(DynamicParticleSystem particleSystem, float emissionSpeed, Vector3f position, Vector3f size) {
		super(particleSystem, emissionSpeed);
		Position = position;
		Size = size;
		// Half scale here to make math more readable down there
		Size.scale(0.5f);
	}

	@Override
	public void emit(int count) {
		for (int i=0; i<count; i++) {
			Particle nextparticle = ParticleSystem.getNextDead();
			if (nextparticle == null)
				return;

			nextparticle.reset();

			newparticleposition.set(Position);
			newparticleposition.x += Size.x * (random.nextFloat()*2-1);
			newparticleposition.y += Size.y * (random.nextFloat()*2-1);
			newparticleposition.z += Size.z * (random.nextFloat()*2-1);
			nextparticle.position.set(newparticleposition);
		}
	}
}
