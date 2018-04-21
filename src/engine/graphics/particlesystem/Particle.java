package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class Particle {
	Vector3f position = new Vector3f(),
			 velocity = new Vector3f();
	float lifetime = Integer.MAX_VALUE;

	public void update(float delta, float timescaler) {
		position.x += velocity.x * timescaler;
		position.y += velocity.y * timescaler;
		position.z += velocity.z * timescaler;
		lifetime += 1f * timescaler;
	}

	public void reset() {
		lifetime = 0;
	}
}