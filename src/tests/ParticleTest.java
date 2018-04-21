package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
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
			Viewport.create(1280, 720, "Engine Testing");
//			Viewport.setVSyncEnabled(false);
//			Viewport.limitFrameRate(false);
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Scene scene = renderer.getScene();

		PointLight light1 = new PointLight();
		light1.setPosition(0,1,0);
		light1.quadraticAttenuation = 0.01f;
		scene.add(light1);

		StaticParticleSystem ps = null;
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D floor = new Model3D("res/models/plane.obj", new LambartMaterial(albedo));
			floor.setPosition(0,-2,0);
			scene.add(floor);

			albedo = TextureManager.newTexture("res\\textures\\sprites\\sonic_3_hd_bubble.png", PixelComponents.RGBA, PixelFormat.SRGBA8);
			albedo.setFilteringMode(TextureFilter.Nearest);
			ps = new StaticParticleSystem(20000, albedo);
			Random r = new Random();
			for (int i=0; i<ps.getMaxParticleCount(); i++) {
				Particle particle = new Particle(
					new Vector3f(
						r.nextFloat()*100f-50,
						r.nextFloat()*100f-50,
						r.nextFloat()*100f-50
					)
				);
				ps.addParticle(particle);
			}
		} catch (IOException | ShaderCompilationException e) {
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setzfar(100);
		camera.setPosition(-1,7,19);
		scene.currentCamera = camera;

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			//sincos += step * timescaler;
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);

			ps.update(delta, timescaler);

			Renderer.setRenderer(renderer);
			Renderer.render();
			ps.render();
			Renderer.swapBuffers();

			Viewport.setTitle("Development Engine " + Viewport.getCurrentFrameRate() + "Hz");

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
