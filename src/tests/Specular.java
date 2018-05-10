package tests;

import engine.graphics.models.ModelFactory;
import engine.graphics.rendering.*;
import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.rendering.DeferredRenderer;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.rendering.DeferredMaterial;
import engine.graphics.textures.PixelComponents;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public final class Specular {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
			Viewport.show();
			//Viewport.setFullScreen(true);
			Viewport.unbindMouseOnBlur(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//engine.logging.Logger.enableMemoryLog(100);

		DeferredRenderer deferredrenderer = null;
		ForwardRenderer forwardrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
			forwardrenderer = Renderer.getForwardRenderer();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			exitCleanly(1);
		}
		Scene scene = deferredrenderer.getScene();
		forwardrenderer.setScene(scene);


		PointLight light1 = new PointLight();
		light1.quadraticAttenuation = 0.032f;
		light1.setPosition(10, 10f , 10);
		light1.setColor(0,1,0);
		scene.add(light1);
		PointLight light2 = new PointLight();
		light2.quadraticAttenuation = 0.032f;
		light2.setPosition(-10, 10f , 10);
		light2.setColor(1,0,0);
		scene.add(light2);

		try {
			for (int z = 0; z <= 10; z++) {
				for (int x = 0; x <= 10; x++) {
					float roughness = z / 10f;
					float specular = x / 10f;
					DeferredMaterial material = deferredrenderer.getNewMaterial();
					material.setAlbedoColor(1, 1, 1, 1f);
					material.setSpecularity(specular);
					material.setRoughness(roughness);
					Model3D model = ModelFactory.getModel("res\\models\\sphere.obj", material);
					model.setPosition(x-5, 0.5f , z-5);
					scene.add(model);
				}
			}

			DeferredMaterial material = deferredrenderer.getNewMaterial();
			Texture albedomap = TextureManager.newTexture("res\\textures\\wood.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D model = ModelFactory.getModel("res\\models\\plane.obj", material);
			material.setTextureRepeat(5,5);
			material.setAlbedoTexture(albedomap);
			material.setRoughness(0.1f);
			material.setSpecularity(1);
			scene.add(model);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.currentCamera = camera;
		camera.setPosition(0, 3, 10);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/600f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			light1.setPosition(6, 10f,(float)Math.cos(sincos)*50f);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			Renderer.swapBuffers();
			deferredrenderer.renderAttachments();


			Viewport.setTitle("Development Engine " + Viewport.getCurrentFrameRate() + "Hz");

			if (Display.isCloseRequested())
				isrunning = false;
			if (!Mouse.isGrabbed() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				isrunning = false;
			if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
				try {
					deferredrenderer.reload();
				} catch (IOException e) {
					e.printStackTrace();
					exitCleanly(1);
				}
			}
		}

		exitCleanly(0);
	}

	static void exitCleanly(int errorcode) {
		Viewport.close();
		System.exit(errorcode);
	}
}