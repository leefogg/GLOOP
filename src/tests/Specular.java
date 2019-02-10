package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.rendering.DeferredRenderer;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.DeferredMaterial;
import GLOOP.graphics.rendering.texturing.PixelComponents;
import GLOOP.graphics.rendering.texturing.PixelFormat;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
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
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		//GLOOP.logging.Logger.enableMemoryLog(100);

		DeferredRenderer deferredrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			exitCleanly(1);
		}
		Scene scene = deferredrenderer.getScene();


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

			Texture albedomap = TextureManager.newTexture("res\\textures\\SOMA\\scanningroom_tiles.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			albedomap.generateAnisotropicMipMaps(100);
			Texture specularmap = TextureManager.newTexture("res\\textures\\SOMA\\scanningroom_tiles_spec.png", PixelComponents.RGB, PixelFormat.R8);
			albedomap.generateAnisotropicMipMaps(100);
			Texture normalmap = TextureManager.newTexture("res\\textures\\SOMA\\scanningroom_tiles_nrm.bmp", PixelComponents.RGB, PixelFormat.RGB8);
			albedomap.generateAnisotropicMipMaps(100);
			DeferredMaterial material = deferredrenderer.getNewMaterial();
			material.setAlbedoTexture(albedomap);
			material.setSpecularMap(specularmap);
			material.setNormalMap(normalmap);
			material.setTextureRepeat(10,10);
			material.setSpecularity(40f);
			material.setRoughness(0.975f);
			Model3D model1 = ModelFactory.getModel("res/models/plane.obj", material);
			scene.add(model1);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
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
			Renderer.render();
			deferredrenderer.debugGBuffer();
			Renderer.swapBuffers();


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