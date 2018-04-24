package engine.graphics.particlesystem;

import engine.Disposable;
import engine.graphics.models.Model2D;
import engine.graphics.models.VertexArray;
import engine.graphics.rendering.Renderer;
import engine.graphics.shading.materials.ParticleMaterial;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.math.MathFunctions;
import engine.math.Quaternion;
import engine.resources.ResourceManager;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

abstract class ParticleSystem implements Disposable {
	protected static VertexArray QuadGeometry = Model2D.getQuadGeometry();
	protected static ParticleMaterial material;
	protected static Matrix4f ModelMatrix = new Matrix4f();
	protected static Quaternion Rotation = new Quaternion();
	protected static Vector3f Scale = new Vector3f(1,1,1);
	private static int InstanceCount=0;

	protected Texture texture;
	protected Particle[] particles;

	public ParticleSystem(int numparticles, Texture texture) throws IOException {
		InstanceCount++;

		material = new ParticleMaterial(texture);

		initializeParticles(numparticles);

		this.texture = texture;
	}

	protected void initializeParticles(int numparticles) {
		particles = new Particle[numparticles];
		for (int i=0; i<particles.length; i++)
			particles[i] = new Particle();
	}

	public final void update(float delta, float timescaler) {
		for (int i=0; i<particles.length; i++)
			particles[i].update(delta, timescaler);
	}

	public abstract void render();

	public int getMaxParticleCount() { return particles.length; }

	protected int getInstanceCount() { return InstanceCount; }

	@Override
	public void requestDisposal() {	ResourceManager.queueDisposal(this);	}

	@Override
	public boolean isDisposed() {
		return material.getShader().isDisposed() || QuadGeometry.isDisposed();
	}

	@Override
	public void dispose() {
		material.getShader().dispose();
	}
}
