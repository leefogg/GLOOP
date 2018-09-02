package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.models.Skybox;
import engine.graphics.rendering.*;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.ChromeMaterial;
import engine.graphics.shading.materials.FullBrightMaterial;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.textures.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class DynamicCubeMapTest {
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

		ForwardRenderer forwardrenderer = Renderer.getForwardRenderer();
		DeferredRenderer deferredrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Scene scene = forwardrenderer.getScene();
		deferredrenderer.setScene(scene);

		PointLight light1 = new PointLight();
		light1.quadraticAttenuation = 0.03f;
		scene.add(light1);

		Vector3f probepos = new Vector3f();
		EnvironmentProbe probe = null;
		Model3D sphere = null;
		try {


			Texture albedo = TextureManager.newTexture("res\\models\\rungholt\\house-RGBA.png", PixelComponents.RGBA, PixelFormat.SRGBA8);
			albedo.setFilteringMode(TextureFilter.Nearest);
			Model3D house = ModelFactory.getModel("res\\models\\rungholt\\house.obj", new LambartMaterial(albedo));
			house.setPosition(0,-15,0);
			house.setScale(2,2,2);
			scene.add(house);

			albedo = TextureManager.newTexture("res/textures/154.JPG", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			Model3D cube = ModelFactory.getModel("res/models/cube.obj", new FullBrightMaterial(albedo));
			cube.setPosition(0,4,0);
			cube.setScale(2,2,2);
			scene.add(cube);

			CubeMap envmap = new CubeMap("environemntmap", 128, PixelFormat.SRGB8);
			probepos = new Vector3f(0,2,0);
			sphere = ModelFactory.getModel("res/models/sphere.obj", new ChromeMaterial(envmap));
			sphere.setPosition(probepos);
			sphere.setScale(4,4,4);
			scene.add(sphere);

			probe = new EnvironmentProbe(envmap, probepos);
			scene.add(probe);


			DeferredMaterial deferredmaterial = deferredrenderer.getNewMaterial();
			deferredmaterial.setSpecularity(1);
			deferredmaterial.setEnvironmentMap(envmap);
			deferredmaterial.setReflectivity(1);
			deferredmaterial.setRoughness(0);
			Model3D othersphere = ModelFactory.getModel("res/models/sphere.obj", deferredmaterial);
			othersphere.setScale(2,2,2);
			othersphere.setPosition(0,1,0);
			scene.add(othersphere);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load scene!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(100);
		camera.setPosition(0,5.5f, 11.5f);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/500f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			probepos.set((float)Math.cos(sincos) * 5, (float)Math.sin(sincos / 10) * 2.5f + 4.5f, (float)Math.sin(sincos) * 5);
			probe.setPosition(probepos);
			sphere.setPosition(probepos);

			Renderer.update();
			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
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

