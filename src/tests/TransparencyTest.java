package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.data.models.Skybox;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.rendering.shading.materials.FullBrightMaterial;
import GLOOP.graphics.rendering.texturing.*;
import GLOOP.general.math.Quaternion;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Random;

public final class TransparencyTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Transparency Test");
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
		Scene scene = deferredrenderer.getScene();
		scene.getAmbientlight().setColor(1,1,1);
		forwardrenderer.setScene(scene);

		Model3D
				bunny = null,
				floor = null;
		Skybox skybox = null;
		try {
			String[] skyboxpaths = new String[] {
					"res\\textures\\skyboxes\\NatureLab\\right.png",
					"res\\textures\\skyboxes\\NatureLab\\left.png",
					"res\\textures\\skyboxes\\NatureLab\\up.png",
					"res\\textures\\skyboxes\\NatureLab\\down.png",
					"res\\textures\\skyboxes\\NatureLab\\back.png",
					"res\\textures\\skyboxes\\NatureLab\\front.png",
			};
			CubeMap cubemaptexture = new CubeMap("cubemap", skyboxpaths, PixelComponents.RGBA, PixelFormat.SRGB8);
			skybox = new Skybox(cubemaptexture);


			DeferredMaterial refractiveballmaterial = deferredrenderer.getNewMaterial();
			refractiveballmaterial.setAlbedoColor(0,0,0,1);
			refractiveballmaterial.setRefractivity(1f);
			refractiveballmaterial.setRefractionIndex(1.03f);
			refractiveballmaterial.setEnvironmentMap(cubemaptexture);
			bunny = ModelFactory.getModel("res\\models\\bunny.obj", refractiveballmaterial);
			bunny.setScale(5,5,5);
			bunny.setPosition(5f, 0f, 0f);


			DeferredMaterial material = deferredrenderer.getNewMaterial();
			material.setAlbedoColor(0,0,0,1);
			Texture albedomap = TextureManager.newTexture("res\\textures\\wood.png", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoMap(albedomap);
			material.setTextureRepeat(10,10);
			material.setEnvironmentMap(cubemaptexture);
			material.setReflectivity(0.2f);
			floor = ModelFactory.getModel("res\\models\\plane.obj", material);

			DeferredMaterial bilboardmaterial = deferredrenderer.getNewMaterial();
			albedomap = TextureManager.newTexture("res\\textures\\transparent\\window.png", PixelComponents.RGBA, PixelFormat.SRGBA8);
			bilboardmaterial.setAlbedoMap(albedomap);
			bilboardmaterial.setReflectivity(0.2f);
			bilboardmaterial.setEnvironmentMap(cubemaptexture);
			Quaternion rotation = new Quaternion();
			rotation.rotate(new Vector3f(1,0,0), -90);
			Random r = new Random();
			for (int i=0; i<5; i++) {
				Model3D bilboardmodel = ModelFactory.getModel("res\\models\\plane.obj", bilboardmaterial);
				bilboardmodel.setRotation(rotation);
				bilboardmodel.setScale(0.05f, 0.05f, 0.05f);
				bilboardmodel.setPosition(0, 2.5f, -4+r.nextFloat()*5);
				scene.add(bilboardmodel);
			}

			FullBrightMaterial mat = new FullBrightMaterial(albedomap);
			for (int i=0; i<5; i++) {
				Model3D bilboardmodel = ModelFactory.getModel("res\\models\\plane.obj", mat);
				bilboardmodel.setRotation(rotation);
				bilboardmodel.setScale(0.05f, 0.05f, 0.05f);
				bilboardmodel.setPosition(-5, 2.5f, -4+r.nextFloat()*5);
				scene.add(bilboardmodel);
			}
		} catch (IOException e) {
			e.printStackTrace(System.err);
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}
		scene.add(bunny);
		scene.add(floor);
		scene.add(skybox);

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setPosition(0,2,8);

		System.gc();

		boolean isrunning = true;
		double sincos = 0, step = (float)Math.PI/100f;
		while (isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;

			Renderer.update();
			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredrenderer.debugGBuffer();
			Renderer.swapBuffers();

			Viewport.setTitle("Transparency Test " + Viewport.getCurrentFrameRate() + "Hz");

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