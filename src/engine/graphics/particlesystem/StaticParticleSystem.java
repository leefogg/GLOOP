package engine.graphics.particlesystem;

import engine.graphics.rendering.Renderer;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.math.MathFunctions;

import java.io.IOException;

public class StaticParticleSystem extends ParticleSystem {
	private int ParticleCount = 0;

	public StaticParticleSystem(int numparticles, Texture texture) throws IOException {
		super(numparticles, texture);
	}

	@Override
	protected void initializeParticles(int numparticles) {
		particles = new Particle[numparticles];
	}

	@Override
	public void render() {
		if (QuadGeometry.isDisposed())
			return;

		material.bind();
		material.commit();
		TextureManager.bindAlbedoMap(texture);
		for (int i=0; i<ParticleCount; i++) {
			Particle particle = particles[i];

			MathFunctions.createTransformationMatrix(particle.position, Rotation, Scale, ModelMatrix);
			material.setCameraAttributes(Renderer.getRenderer().getScene().currentCamera, ModelMatrix);
			QuadGeometry.render();
		}
	}

	public void addParticle(Particle particle) {
		if (ParticleCount >= particles.length)
			throw new ArrayIndexOutOfBoundsException("No more particles remaining to assign.");

		particles[ParticleCount++] = particle;
	}
}
