package engine.graphics.particlesystem;

import engine.graphics.models.DataVolatility;
import engine.graphics.rendering.Renderer;
import engine.graphics.textures.Texture;

import java.io.IOException;
import java.nio.BufferOverflowException;

public class StaticParticleSystem extends ParticleSystem {
	private int ParticleCount = 0;

	public StaticParticleSystem(Particle[] particles, Texture texture) throws IOException {
		super(particles, texture, DataVolatility.Static);

		addParticles(particles);

		Renderer.checkErrors();
	}

	@Override
	public void render() {
		render(ParticleCount);
	}

	public void addParticles(Particle[] particles) {
		addParticles(particles, 0, particles.length);
	}
	public void addParticles(Particle[] particles, int startindex, int length) {
		if (startindex < 0)
			throw new IllegalArgumentException("Start index must be greater than 0");
		if (startindex + length > particles.length)
			throw new IllegalArgumentException("Start index + length is larger than the size of the provided array");
		if (ParticleCount + length > getMaxParticleCount())
			throw new BufferOverflowException();

		updateParticles(particles, ParticleCount, startindex, length);

		ParticleCount += length;
	}

	public void updateParticles(Particle[] particles, int index){
		updateParticles(particles, index, 0, particles.length);
	}

	public void updateParticles(Particle[] particles, int index, int startindex, int length) {
		positionsbuffer.update(getPositionsBuffer(particles), index, startindex, length);
	}
}
