package gloop.graphics.rendering.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class RectangleEmitter extends ParticleEmitter {
	private final Vector3f position;
	private final Vector3f size;

	public RectangleEmitter(DynamicParticleSystem particleSystem, Vector3f position, Vector3f size) {
		super(particleSystem);
		this.position = position;
		this.size = size;
		// Half scale here to make math more readable down there
		this.size.scale(0.5f);
	}

	@Override
	public void emit(int count) {
		for (int i=0; i<count; i++) {
			Particle nextparticle = particleSystem.getNextDead();
			if (nextparticle == null)
				return;

			nextparticle.reset();

			TEMP_VECTOR.set(position);
			TEMP_VECTOR.x += size.x * (RANDOM.nextFloat()*2-1);
			TEMP_VECTOR.y += size.y * (RANDOM.nextFloat()*2-1);
			TEMP_VECTOR.z += size.z * (RANDOM.nextFloat()*2-1);
			nextparticle.position.set(TEMP_VECTOR);
		}
	}
}
