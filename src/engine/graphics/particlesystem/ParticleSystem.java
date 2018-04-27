package engine.graphics.particlesystem;

import engine.Disposable;
import engine.graphics.cameras.Camera;
import engine.graphics.data.DataConversion;
import engine.graphics.models.*;
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
	protected static final VertexArray QuadGeometry = Model2D.getQuadGeometry();
	protected static ParticleMaterial material;
	protected static final Matrix4f ModelMatrix = new Matrix4f();
	protected static final Quaternion Rotation = new Quaternion();
	protected static final Vector3f Scale = new Vector3f(1,1,1);
	private static int InstanceCount=0;

	protected final Texture texture;
	private final int MaxParticles;
	//TODO: Make these final
	protected VertexArray data;
	protected VertexBuffer positionsbuffer;

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
		this.texture = texture;
		material = new ParticleMaterial(texture);

		InstanceCount++;
	}

	private void constructVertexArray(int numparticles,DataVolatility volatility) {
		data = new VertexArray("ParticleSystemBuffer" + InstanceCount);
		Renderer.checkErrors();
		data.storeStriped(DataConversion.toGLBuffer(new float[] {
						0,-1,0,	0,0, // Top left
						1,-1,0,	1,0, // Top right
						0,0,0,	0,1, // Bottom left
						1,0,0,	1,1  // Bottom right
				}),
				new int[] {3,2},
				new boolean[] {false, false},
				0);

		positionsbuffer = new VertexBuffer(GLArrayType.Array, numparticles * 3 * 4, volatility, DataType.Float);
		data.bindAttribute(positionsbuffer, 2,3,3,0,true);
		data.setRenderingMode(RenderMode.TriangleStrip);
	}

	protected float[] getPositionsBuffer(Particle[] particles) {
		float[] translations = new float[particles.length * 3];
		for (int i=0, x=0; i<particles.length; i++) {
			Particle p = particles[i];
			MathFunctions.createTransformationMatrix(p.position, Rotation, Scale, ModelMatrix);
			// Just uploading translation part for now
			translations[x++] = ModelMatrix.m30;
			translations[x++] = ModelMatrix.m31;
			translations[x++] = ModelMatrix.m32;
		}

		return translations;
	}

	public void render() {
		render(MaxParticles);
	}
	protected void render(int particlecount) {
		if (data.isDisposed())
			return;
		if (particlecount == 0)
			return;

		Camera camera = Renderer.getRenderer().getScene().currentCamera;
		material.bind();
		material.setProjectionMatrix(camera.getProjectionMatrix());
		material.setViewMatrix(camera.getViewMatrix());
		material.commit();
		TextureManager.bindAlbedoMap(texture);
		data.renderInstanced(particlecount);
	}

	public int getMaxParticleCount() { return MaxParticles; }

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
