package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class RectangleEmitter extends ParticleEmitter {
	private Vector3f
			Position,
			Size;

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

			TempVector.set(Position);
			TempVector.x += Size.x * (random.nextFloat()*2-1);
			TempVector.y += Size.y * (random.nextFloat()*2-1);
			TempVector.z += Size.z * (random.nextFloat()*2-1);
			nextparticle.position.set(TempVector);
		}
	}
}
