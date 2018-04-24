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
import org.lwjgl.opengl.GL31;

import java.io.IOException;
import java.nio.BufferOverflowException;

public class StaticParticleSystem extends ParticleSystem {
	private int ParticleCount = 0;
	private VertexArray data;
	VertexBuffer positionsbuffer;

	public StaticParticleSystem(Particle[] particles, Texture texture) throws IOException {
		super(particles.length, texture);

		this.particles = particles;
		constructVertexArray();

		Renderer.checkErrors();
	}

	private void constructVertexArray() {
		data = new VertexArray("ParticleSystemBuffer" + getInstanceCount());
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

		positionsbuffer = new VertexBuffer(GLArrayType.Array);
		positionsbuffer.store(DataConversion.toGLBuffer(getPositionsBuffer(particles)));
		data.bindAttribute(positionsbuffer, 2,3,3,0,true);
		data.setRenderingMode(RenderMode.TriangleStrip);
	}

	@Override
	protected void initializeParticles(int numparticles) {
		// Handled by custom constructor
	}

	@Override
	public void render() {
		if (data.isDisposed())
			return;
		if (ParticleCount == 0)
			return;

		Camera camera = Renderer.getRenderer().getScene().currentCamera;
		material.bind();
		material.setProjectionMatrix(camera.getProjectionMatrix());
		material.setViewMatrix(camera.getViewMatrix());
		material.commit();
		TextureManager.bindAlbedoMap(texture);
		data.bind();
		GL31.glDrawArraysInstanced(GL11.GL_TRIANGLE_STRIP, 0, 4, Math.min(ParticleCount, getMaxParticleCount()));
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
