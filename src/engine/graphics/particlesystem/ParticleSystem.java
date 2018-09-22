package engine.graphics.particlesystem;

import engine.general.Disposable;
import engine.graphics.cameras.Camera;
import engine.graphics.data.DataConversion;
import engine.graphics.models.*;
import engine.graphics.rendering.Renderable;
import engine.graphics.rendering.Renderer;
import engine.graphics.shading.materials.ParticleMaterial;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.math.Quaternion;
import engine.resources.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.nio.FloatBuffer;

public abstract class  ParticleSystem implements Renderable, Disposable{
	protected static VertexBuffer QuadGeometry = getQuadBufferSingleton();
	protected static ParticleMaterial material;
	protected static final Vector3f Scale = new Vector3f(1,1,1);
	private static int InstanceCount = 0;

	protected final Texture texture;
	private final int MaxParticles;
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
		MaxParticles = numparticles;
		translations = BufferUtils.createFloatBuffer(MaxParticles * 3);
		this.texture = texture;
		material = new ParticleMaterial(texture);

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

	public static final VertexBuffer getQuadBufferSingleton() {
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
		for (int i=0; i<particles.length; i++) {
			Particle p = particles[i];
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
		render(MaxParticles);
	}
	protected void render(int particlecount) {
		if (data.isDisposed())
			return;
		if (particlecount == 0)
			return;

		Camera camera = Renderer.getCurrentCamera();
		material.bind();
		material.setProjectionMatrix(camera.getProjectionMatrix());
		material.setViewMatrix(camera.getViewMatrix());
		material.commit();
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

	public int getMaxParticleCount() { return MaxParticles; }

	@Override
	public void getModelMatrix(Matrix4f out) { out.setIdentity(); } // Shader doesn't use this anyway

	@Override
	public void requestDisposal() {	ResourceManager.queueDisposal(this);	}

	@Override
	public boolean isDisposed() {
		return material.getShader().isDisposed() || QuadGeometry.isDisposed() || positionsbuffer.isDisposed();
	}

	@Override
	public void dispose() {
		data.dispose();
		material.getShader().dispose();
	}
}
