package gloop.graphics.rendering.particlesystem;

import gloop.graphics.data.models.DataVolatility;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.texturing.Texture;

import java.io.IOException;
import java.nio.BufferOverflowException;

public class StaticParticleSystem extends ParticleSystem {
	private int particleCount = 0;

	public StaticParticleSystem(Particle[] particles, Texture texture) throws IOException {
		super(particles, texture, DataVolatility.Static);

		addParticles(particles);

		Renderer.checkErrors();
	}

	@Override
	public void update(float delta, float timescaler) {

	}

	@Override
	public void render() {
		render(particleCount);
	}

	public void addParticles(Particle[] particles) {
		addParticles(particles, 0, particles.length);
	}
	public void addParticles(Particle[] particles, int startindex, int length) {
		if (startindex < 0)
			throw new IllegalArgumentException("Start index must be greater than 0");
		if (startindex + length > particles.length)
			throw new IllegalArgumentException("Start index + length is larger than the size of the provided array");
		if (particleCount + length > getMaxParticleCount())
			throw new BufferOverflowException();

		updateParticles(particles, particleCount, startindex, length);

		particleCount += length;
	}


}
