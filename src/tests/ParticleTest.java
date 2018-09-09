package tests;

import engine.general.exceptions.UnsupportedException;
import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.particlesystem.DynamicParticleSystem;
import engine.graphics.particlesystem.OmniEmitter;
import engine.graphics.particlesystem.Particle;
import engine.graphics.particlesystem.StaticParticleSystem;
import engine.graphics.rendering.ForwardRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.textures.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Random;

public final class ParticleTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Particle Testing");
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Scene scene = renderer.getScene();

		PointLight light1 = new PointLight();
		light1.setPosition(0,2,0);
		light1.quadraticAttenuation = 0.01f;
		scene.add(light1);

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(200);
		camera.setPosition(-1,53,148);

		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			floor.setPosition(0,-2,0);
			scene.add(floor);

			Particle[] particles = new Particle[10000];
			Random r = new Random();
			for (int i=0; i<particles.length; i++) {
				Particle particle = new Particle(
						new Vector3f(
								r.nextFloat()*50f,
								r.nextFloat()*100f,
								r.nextFloat()*100f-50
						)
				);
				particles[i] = particle;
			}

			albedo = TextureManager.newTexture("res\\textures\\sprites\\sonic_3_hd_bubble.png", PixelComponents.RGBA, PixelFormat.SRGBA8);
			albedo.setFilteringMode(TextureFilter.Nearest);
			StaticParticleSystem sps = new StaticParticleSystem(particles, albedo);
			scene.add(sps);

			DynamicParticleSystem dps = new DynamicParticleSystem(100000, albedo);
			scene.add(dps);
			OmniEmitter omniemitter = new OmniEmitter(dps, new Vector3f(-25, 10,0), new Vector3f(0,.1f, 0));
			omniemitter.setEmitVelocityError(new Vector3f(.1f, .1f, .1f));
			dps.setParticleLifeTime(1);
			omniemitter.setEmmisionSpeed(25);
			scene.add(omniemitter);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		System.gc();

		boolean isrunning = true;
		Viewport.update();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			scene.update(delta, timescaler);

			Renderer.setRenderer(renderer);
			Renderer.render();
			Renderer.swapBuffers();

			Viewport.setTitle("Particle Testing " + Viewport.getCurrentFrameRate() + "Hz");

			if (Display.isCloseRequested())
				isrunning = false;
			if (!Mouse.isGrabbed() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				isrunning = false;
		}

		exitCleanly(0);
	}

	static void exitCleanly(int errorcode) {
		Viewport.close();
		System.exit(errorcode);
	}
}
