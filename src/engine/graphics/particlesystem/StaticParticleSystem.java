package engine.graphics.particlesystem;

import engine.graphics.cameras.Camera;
import engine.graphics.data.DataConversion;
import engine.graphics.models.GLArrayType;
import engine.graphics.models.RenderMode;
import engine.graphics.models.VertexArray;
import engine.graphics.models.VertexBuffer;
import engine.graphics.rendering.Renderer;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.math.MathFunctions;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL31;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class StaticParticleSystem extends ParticleSystem {
	private int ParticleCount = 0;
	private VertexArray data;
	VertexBuffer positionsbuffer;

	public StaticParticleSystem(Particle[] particles, Texture texture) throws IOException {
		super(particles.length, texture);

		this.particles = particles;
		ParticleCount = particles.length;
		constructVertexArray();

		Renderer.checkErrors();
	}

	private void constructVertexArray() {
		this.data = new VertexArray("ParticleSystemBuffer" + getInstanceCount());
		Renderer.checkErrors();
		this.data.storeStriped(DataConversion.toGLBuffer(new float[] {
					0,-1,0,	0,0, // Top left
					1,-1,0,	1,0, // Top right
					0,0,0,	0,1, // Bottom left
					1,0,0,	1,1  // Bottom right
				}),
				new int[] {3,2},
				new boolean[] {false, false},
				0);

		positionsbuffer = new VertexBuffer(GLArrayType.Array);
		positionsbuffer.store(DataConversion.toGLBuffer(getPositionsBuffer(particles)));
		data.bindAttribute(positionsbuffer, 2,3,3,0,true);
		this.data.setRenderingMode(RenderMode.TriangleStrip);
	}

	@Override
	protected void initializeParticles(int numparticles) {
		// Handled by custom constructor
	}

	Vector3f zero = new Vector3f();
	@Override
	public void render() {
		if (data.isDisposed())
			return;

		Camera camera = Renderer.getRenderer().getScene().currentCamera;
		material.bind();
		material.setProjectionMatrix(camera.getProjectionMatrix());
		material.setViewMatrix(camera.getViewMatrix());
		material.commit();
		TextureManager.bindAlbedoMap(texture);
		data.bind();
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, particles.length);
	}

	public void addParticle(Particle particle) {
		if (ParticleCount >= particles.length)
			throw new ArrayIndexOutOfBoundsException("No more particles remaining to assign.");

		particles[ParticleCount++] = particle;
	}

	private float[] getPositionsBuffer(Particle[] particles) {
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
}
