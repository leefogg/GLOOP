package engine.graphics.particlesystem;

import engine.graphics.cameras.Camera;
import engine.graphics.models.DataVolatility;
import engine.graphics.rendering.Renderer;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;

import java.io.IOException;

// TODO: Implement Renderable
public class DynamicParticleSystem extends ParticleSystem {
	private int lastDead = 0;
	private int LifeTime = 200;

	public DynamicParticleSystem(int numparticles, Texture texture) throws IOException {
		super(numparticles, texture, DataVolatility.Stream);
	}

	@Override
	public void update(float delta, float timescaler) {
		super.update(delta, timescaler);
		positionsbuffer.update(getPositionsBuffer(particles), 0);
	}

	@Override
	public void render() {
		if (QuadGeometry.isDisposed())
			return;

		Camera camera = Renderer.getRenderer().getScene().currentCamera;
		material.bind();
		material.setProjectionMatrix(camera.getProjectionMatrix());
		material.setViewMatrix(camera.getViewMatrix());
		material.commit();
		TextureManager.bindAlbedoMap(texture);
		data.renderInstanced(particles.length);
	}

	public Particle getNextDead() {
		int start = lastDead % particles.length;
		for(; lastDead<particles.length; lastDead++)
			if (particleIsDead(particles[lastDead]))
				return particles[lastDead];
		for(lastDead = 0; lastDead<start; lastDead++)
			if (particleIsDead(particles[lastDead]))
				return particles[lastDead];

		return null;
	}

	private boolean particleIsDead(Particle particle) {
		return particle.lifetime > LifeTime;
	}

	public void setParticleLifeTime(int frames) { LifeTime = frames; }
}
