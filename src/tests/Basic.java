package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.rendering.ForwardRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.shading.posteffects.FXAAPostEffect;
import engine.graphics.textures.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public final class Basic {
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
		Renderer.setRenderer(renderer);

		PointLight light1 = new PointLight();
		light1.quadraticAttenuation = 0.01f;
		scene.add(light1);


		Model3D model1 = null;
		Model3D model2 = null;
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			albedo.generateAnisotropicMipMaps(100);
			model1 = new Model3D("res/models/plane.obj", new LambartMaterial(albedo));

			albedo = TextureManager.newTexture("res/textures/charizard.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			model2 = new Model3D("res/models/charizard.obj", new LambartMaterial(albedo));
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}
		scene.add(model1);
		scene.add(model2);

		DebugCamera camera = new DebugCamera();
		camera.setzfar(100);
		camera.setPosition(-1,7,19);
		Renderer.setCamera(camera);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(isrunning) {
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			//sincos += step * timescaler;
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);

			Renderer.setRenderer(renderer);
			Renderer.render();
			Renderer.swapBuffers();

			Viewport.update();
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

