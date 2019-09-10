package gloop.graphics.rendering.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class Particle {
	Vector3f position = new Vector3f(),
			 velocity = new Vector3f();
	float lifetime = Integer.MAX_VALUE; // Start dead

	public Particle(){}
	public Particle(Vector3f position) {
		if (position != null)
			this.position = position;
	}

	public void update(float delta, float timescaler) {
		position.x += velocity.x * timescaler;
		position.y += velocity.y * timescaler;
		position.z += velocity.z * timescaler;
		lifetime = (lifetime+timescaler < 0) ? Integer.MAX_VALUE : lifetime+timescaler;
	}

	public void reset() {
		lifetime = 0;
	}
}
