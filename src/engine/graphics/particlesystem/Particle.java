package engine.graphics.particlesystem;

import org.lwjgl.util.vector.Vector3f;

public class Particle {
	Vector3f position = new Vector3f(),
			 velocity = new Vector3f();
	float lifetime = Integer.MAX_VALUE; // Dead

	public void update() {
		position.x += velocity.x;
		position.y += velocity.y;
		position.z += velocity.z;
		lifetime++;
	}

	public void reset() {
		lifetime = 0;
	}
}
