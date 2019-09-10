package gloop.graphics.rendering.particlesystem;

import gloop.resources.Disposable;
import gloop.graphics.cameras.Camera;
import gloop.graphics.data.DataConversion;
import gloop.graphics.data.DataType;
import gloop.graphics.data.models.*;
import gloop.graphics.rendering.Renderable;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.shading.materials.ParticleMaterial;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import gloop.resources.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class  ParticleSystem implements Renderable, Disposable{
	protected static VertexBuffer QuadGeometry = getQuadBufferSingleton();
	protected static ParticleMaterial Material;
	private static int InstanceCount = 0;

	protected final Texture texture;
	private final int maxParticles;
	//TODO: Make these final
	protected VertexArray data;
	protected VertexBuffer positionsbuffer;
	private final FloatBuffer translations;

	public ParticleSystem(int numparticles, Texture texture, DataVolatility volatility) throws IOException {
		this(numparticles, texture);

		constructVertexArray(numparticles, volatility);
	}
	public ParticleSystem(Particle[] particles, Texture texture, DataVolatility volatility) throws IOException {
		this(particles.length, texture);

		constructVertexArray(particles.length, volatility);
	}
	private ParticleSystem(int numparticles, Texture texture) throws IOException {
		maxParticles = numparticles;
		translations = BufferUtils.createFloatBuffer(maxParticles * 3);
		this.texture = texture;
		Material = new ParticleMaterial(texture);

		InstanceCount++;
	}

	private void constructVertexArray(int numparticles, DataVolatility volatility) {
		data = new VertexArray("ParticleSystemBuffer" + InstanceCount);
		data.storeStriped(getQuadBufferSingleton(), 5,
				new int[] {3,2},
				new boolean[] {false, false},
				0, 4*5);
		Renderer.checkErrors();

		positionsbuffer = new VertexBuffer(GLArrayType.Array, numparticles * 3 * 4, volatility, DataType.Float);
		data.bindAttribute(positionsbuffer, 2,3,3,0,true);
		data.setRenderingMode(RenderMode.TriangleStrip);
	}

	public static VertexBuffer getQuadBufferSingleton() {
		if (QuadGeometry == null) {
			QuadGeometry = new VertexBuffer(GLArrayType.Array);
			float[] quaddata = new float[] {
					0,-1,0,	0,0, // Top left
					1,-1,0,	1,0, // Top right
					0,0,0,	0,1, // Bottom left
					1,0,0,	1,1  // Bottom right
			};
			QuadGeometry.store(DataConversion.toGLBuffer(quaddata));
		}

		return QuadGeometry;
	}

	protected FloatBuffer getPositionsBuffer(Particle[] particles) {
		translations.rewind();
		for (Particle p : particles) {
			// Just uploading translation part for now
			translations.put(p.position.x);
			translations.put(p.position.y);
			translations.put(p.position.z);
		}

		translations.flip();

		return translations;
	}

	@Override
	public void render() {
		render(maxParticles);
	}
	protected void render(int particlecount) {
		if (data.isDisposed())
			return;
		if (particlecount == 0)
			return;

		Camera camera = Renderer.getCurrentCamera();
		Material.bind();
		Material.setProjectionMatrix(camera.getProjectionMatrix());
		Material.setViewMatrix(camera.getViewMatrix());
		Material.commit();
		TextureManager.bindAlbedoMap(texture);
		data.renderInstanced(particlecount);
	}

	public void updateParticles(Particle[] particles){
		updateParticles(particles, 0);
	}
	public void updateParticles(Particle[] particles, int startelement){
		updateParticles(particles, startelement, 0, particles.length);
	}

	public void updateParticles(Particle[] particles, int startelement, int startindex, int length) {
		positionsbuffer.update(getPositionsBuffer(particles), startelement);
	}

	public int getMaxParticleCount() { return maxParticles; }

	@Override
	public void getModelMatrix(Matrix4f out) { out.setIdentity(); } // Shader doesn't use this anyway

	@Override
	public void requestDisposal() {	ResourceManager.queueDisposal(this);	}

	@Override
	public boolean isDisposed() {
		return Material.getShader().isDisposed() || QuadGeometry.isDisposed() || positionsbuffer.isDisposed();
	}

	@Override
	public void dispose() {
		data.dispose();
		Material.getShader().dispose();
	}
}
