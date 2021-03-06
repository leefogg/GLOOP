package tests;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.Settings;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.data.models.Model2D;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.*;
import gloop.graphics.rendering.shading.lights.DirectionalLight;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.lights.SpotLight;
import gloop.graphics.rendering.shading.materials.FullBrightMaterial;
import gloop.graphics.rendering.shading.materials.SingleColorMaterial;
import gloop.graphics.rendering.texturing.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;
import gloop.general.math.Quaternion;
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
			Renderer.init();
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
		scene.getAmbientlight().setColor(0.1f, 0.03f, 0.03f);

		DirectionalLight directionallight = new DirectionalLight();
		PointLight pointlight = new PointLight();
		SpotLight spotlight = new SpotLight();
		{

			//shadowlight.setPosition(20,20,-20);
			pointlight.enableShadows(256, 1, 200);
			pointlight.setColor(0.5f, 0.5f, 1);
			pointlight.quadraticAttenuation = 0.001f;
			scene.add(pointlight);

			directionallight.setDiffuseColor(0.1f,0.1f,0.1f);
			directionallight.setDirection(0.01f, -0.5f, 0.5f);
			directionallight.enableShadows(2048, 10,100);
			scene.add(directionallight);

			spotlight.setPosition(125,50,50);
			spotlight.lookAt(125,0,0);
			spotlight.enableShadows(2048,10,300);
			spotlight.setColor(0,1,1);
			spotlight.setQuadraticAttenuation(0.001f);
			scene.add(spotlight);
		}

		Model3D lightsphere = null;
		Model2D shadowTexture = null;
		Model3D teapot = null;
		try {
			Texture defaulttextre = TextureManager.newTexture("res\\textures\\default.png", PixelComponents.RGB, PixelFormat.SRGB8);

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
				deferredmaterial.setAlbedoMap(defaulttextre);
				Model3D cube2 = ModelFactory.getModel("res\\models\\cube.obj", deferredmaterial);
				cube2.setPosition(randompos);
				cube2.setRotation(randomrot);
				cube2.setScale(randomscale);
				scene.add(cube2);
			}

			DeferredMaterial floormaterial = deferredrenderer.getNewMaterial();
			floormaterial.setAlbedoColor(1, 1, 1, 1);
			floormaterial.setAlbedoMap(defaulttextre);
			floormaterial.setAlbedoColor(1, 1, 1, 1);
			floormaterial.setTextureRepeat(2, 2);
			floormaterial.setSpecularity(1);
			floormaterial.setRoughness(0.5f);
			Model3D box = ModelFactory.getModel("res\\models\\insideout box.obj", floormaterial);
			box.setScale(50,50,50);
			scene.add(box);

			SingleColorMaterial white =  new SingleColorMaterial();
			white.setColor(1,1,1);
			lightsphere = ModelFactory.getModel("res\\models\\sphere.obj", white);
			scene.add(lightsphere);

			// Directional light tests scene
			DeferredMaterial deferredMaterial = deferredrenderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			deferredMaterial.setAlbedoMap(albedo);
			deferredMaterial.setTextureRepeat(5,5);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", deferredMaterial);
			floor.setPosition(100,0,0);
			scene.add(floor);

			deferredMaterial = deferredrenderer.getNewMaterial();
			deferredMaterial.setAlbedoColor(1,1,1,1);
			teapot = ModelFactory.getModel("res/models/teapot.obj", deferredMaterial);
			teapot.setPosition(75,0,0);
			teapot.setScale(24,24,24);
			scene.add(teapot);

			deferredMaterial = deferredrenderer.getNewMaterial();
			deferredMaterial.setAlbedoColor(1,1,1,1);
			Model3D bunny = ModelFactory.getModel("res/models/bunny.obj", deferredMaterial);
			bunny.setPosition(125,0,0);
			bunny.setScale(25,25,25);
			scene.add(bunny);

			shadowTexture = new Model2D(0,0,1280/4, 720/4);
			((FullBrightMaterial)shadowTexture.getMaterial()).setAlbedoTexture(spotlight.getShadowMap());
			scene.add(shadowTexture);
		} catch (IOException e) {
			System.err.println("Couldn't load Model!");
			e.printStackTrace(System.err);
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setzfar(100);
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setPosition(100, 12, 50);
		//camera.setRotation(12,3,0);

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
			pointlight.setPosition(lightpos);
			lightsphere.setPosition(lightpos);
			//spotLight.setOuterCone(170);
			//spotLight.setPosition(100 + (float)Math.sin(sincos) * 25,100,50);
			//spotLight.lookAt(100,0,0);

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
