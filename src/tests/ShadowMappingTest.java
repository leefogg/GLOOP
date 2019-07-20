package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.Settings;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.materials.SingleColorMaterial;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import GLOOP.general.math.Quaternion;
import java.io.IOException;
import java.util.Random;

public class ShadowMappingTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
			Viewport.show();
			Settings.EnableHDR = true;
			Settings.EnableShadows = true;
			//Viewport.setVSyncEnabled(false);
			//Viewport.limitFrameRate(false);
			Renderer.Init();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		DeferredRenderer deferredrenderer = null;
		ForwardRenderer forwardRenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
			forwardRenderer = Renderer.getForwardRenderer();
		} catch (Exception e) {
			e.printStackTrace();
			exitCleanly(1);
		}
		Renderer.setRenderer(forwardRenderer);
		Scene scene = deferredrenderer.getScene();
		forwardRenderer.setScene(scene);
		scene.getAmbientlight().setColor(0.03f, 0.03f, 0.03f);

		PointLight shadowlight = new PointLight();
		//shadowlight.setPosition(20,20,-20);
		shadowlight.SetShadowMapEnabled(true);
		shadowlight.setColor(0.5f,0.5f,1);
		shadowlight.quadraticAttenuation = 0.001f;
		scene.add(shadowlight);

		Model3D lightsphere = null;
		try {
			Texture walltexture = TextureManager.newTexture("res\\textures\\default.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Texture fencetexture = TextureManager.newTexture("res\\textures\\fence.png", PixelComponents.RGBA, PixelFormat.SRGBA8);

			Random r = new Random(2);
			Vector3f randompos = new Vector3f();
			Quaternion randomrot = new Quaternion();
			Vector3f randomscale = new Vector3f();

			for (int i=0; i<100; i++) {
				randompos.set(r.nextFloat() * 50 - 25, r.nextFloat()* 50 - 25, r.nextFloat()* 50 -25);
				randomrot.toIdentity();
				randomrot.rotate(r.nextFloat() * 360, r.nextFloat() * 360 , r.nextFloat() * 360);
				randomscale.set(r.nextFloat() * 3, r.nextFloat() * 3, r.nextFloat() * 3);

				DeferredMaterial deferredmaterial = deferredrenderer.getNewMaterial();
				deferredmaterial.setAlbedoColor(1,1,1,1);
				deferredmaterial.setAlbedoMap(fencetexture);
				Model3D cube2 = ModelFactory.getModel("res\\models\\cube.obj", deferredmaterial);
				cube2.setPosition(randompos);
				cube2.setRotation(randomrot);
				cube2.setScale(randomscale);
				scene.add(cube2);
			}

			DeferredMaterial floormaterial = deferredrenderer.getNewMaterial();
			floormaterial.setAlbedoColor(1, 1, 1, 1);
			floormaterial.setAlbedoMap(walltexture);
			floormaterial.setAlbedoColor(1, 1, 1, 1);
			floormaterial.setTextureRepeat(2, 2);
			floormaterial.setSpecularity(1);
			floormaterial.setRoughness(0.5f);
			Model3D floor = ModelFactory.getModel("res\\models\\insideout box.obj", floormaterial);
			floor.setScale(50,50,50);
			scene.add(floor);

			SingleColorMaterial white =  new SingleColorMaterial();
			white.setColor(1,1,1);
			lightsphere = ModelFactory.getModel("res\\models\\sphere.obj", white);
			scene.add(lightsphere);

			//scene.add(new Skybox(shadowlight.getShadowMap()));
		} catch (IOException e) {
			System.err.println("Couldn't load Model!");
			e.printStackTrace(System.err);
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}


		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setPosition(0, 0, 70);


		System.gc();

		boolean isrunning = true;
		float sincos = 0;
		float step = (float)Math.PI / 360;
		Vector3f lightpos = new Vector3f();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step;

			lightpos.set((float)Math.sin(sincos * 0.98) * 20, (float)Math.sin(sincos * 1.23f) * 20, (float)Math.cos(sincos * 1.17) * 20);
			shadowlight.setPosition(lightpos);
			lightsphere.setPosition(lightpos);

			Renderer.update();

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardRenderer);
			Renderer.render();
			deferredrenderer.debugGBuffer();
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
