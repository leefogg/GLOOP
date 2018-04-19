package engine.graphics.particlesystem;

import engine.graphics.models.Model2D;
import engine.graphics.models.VertexArray;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.materials.ParticleShader;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import engine.math.MathFunctions;
import engine.math.Quaternion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

// TODO: Implement Disposable
// TODO: Implement Renderable
public class ParticleSystem {
	private static VertexArray QuadGeometry = Model2D.getQuadGeometry();
	private static ParticleShader Shader;
	private static Matrix4f TempModelMatrix = new Matrix4f();
	private static Quaternion Rotation = new Quaternion();
	private static Vector3f Scale = new Vector3f(1,1,1);

	static {
		try {
			Shader = new ParticleShader();
		} catch (Exception e) {
			e.printStackTrace();
			Viewport.close();
			System.exit(1);
		}
	}


	private Texture texture;
	private int lastDead = 0;
	private Particle[] particles;
	private float LifeTime = 200;

	public ParticleSystem(int numparticles, Texture texture) {
		particles = new Particle[numparticles];
		for (int i=0; i<particles.length; i++)
			particles[i] = new Particle();

		this.texture = texture;
	}

	public void update() {
		for (int i=0; i<particles.length; i++)
			particles[i].update();
	}

	public void render() {
		if (QuadGeometry.isDisposed())
			return;

		Shader.bind();
		TextureManager.bindAlbedoMap(texture);
		for (int i=0; i<particles.length; i++) {
			Particle particle = particles[i];
			if (particleIsDead(particle))
				continue;

			MathFunctions.createTransformationMatrix(particle.position, Rotation, Scale, TempModelMatrix);
			Shader.setCameraUniforms(Renderer.getRenderer().getScene().currentCamera, TempModelMatrix);
			QuadGeometry.render();
		}
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
}
