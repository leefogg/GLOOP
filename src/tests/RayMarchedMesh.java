package tests;

import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.*;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.texturing.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import tests.Materials.RayMarchMaterial;

public final class RayMarchedMesh {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		DeferredRenderer deferredrenderer = null;
		ForwardRenderer forwardrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
			forwardrenderer = Renderer.getForwardRenderer();
		} catch (Exception e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setPosition(0,1,15);
		camera.setzfar(100);
		deferredrenderer.getScene().setGameCamera(camera);
		Scene scene = deferredrenderer.getScene();

		PointLight light1 = new PointLight();
		light1.setPosition(0,1,0);
		light1.quadraticAttenuation = 0.01f;
		scene.add(light1);

		try {
			DeferredMaterial material = deferredrenderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res/textures/154.JPG", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			material.setAlbedoMap(albedo);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", material);
			floor.setPosition(0,-3,0);
			scene.add(floor);

			material = deferredrenderer.getNewMaterial();
			albedo = TextureManager.newTexture("res/textures/charizard.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			material.setAlbedoMap(albedo);
			Model3D model2 = ModelFactory.getModel("res/models/charizard.obj", material);
			model2.setPosition(12,-3,0);
			scene.add(model2);

			Model3D cubeproxy = ModelFactory.getModel("res/models/cube.obj", new RayMarchMaterial());
			cubeproxy.setScale(7,7,7);
			scene.add(cubeproxy);
		} catch (Exception e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		System.gc();

		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(true) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredrenderer.debugGBuffer();
			Renderer.swapBuffers();

			Viewport.setTitle("Development Engine " + Viewport.getCurrentFrameRate() + "Hz");

			if (Display.isCloseRequested())
				break;
			if (!Mouse.isGrabbed() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				break;
		}

		exitCleanly(0);
	}

	static void exitCleanly(int errorcode) {
		Viewport.close();
		System.exit(errorcode);
	}
}

