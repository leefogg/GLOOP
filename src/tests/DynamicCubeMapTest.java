package tests;

import gloop.general.exceptions.UnsupportedException;
import gloop.general.math.Quaternion;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.*;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.materials.ChromeMaterial;
import gloop.graphics.rendering.shading.materials.LambartMaterial;
import gloop.graphics.rendering.texturing.*;
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
		DeferredRenderer deferredRenderer;
		try {
			 deferredRenderer = Renderer.getDeferedRenderer();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Scene scene = forwardrenderer.getScene();
		deferredRenderer.setScene(scene);

		PointLight light = new PointLight();
		light.setPosition(1,12,1);
		light.quadraticAttenuation = 0.0001f;
		scene.add(light);

		Model3D cube = null;
		try {
			Texture wallstexture = TextureManager.newTexture("res\\textures\\portal\\concrete_modular_wall001_gradient00.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			Texture floortexture = TextureManager.newTexture("res\\textures\\portal\\concrete_modular_floor001c.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D floor = ModelFactory.getModel("res/models/portal/floor.obj", new LambartMaterial(floortexture));
			scene.add(floor);
			Model3D ceiling = ModelFactory.getModel("res/models/portal/floor.obj", new LambartMaterial(floortexture));
			Quaternion rotation = new Quaternion();
			rotation.rotate(180,0,0);
			ceiling.setRotation(rotation);
			ceiling.setPosition(0,30,0);
			scene.add(ceiling);
			Model3D redlevel = ModelFactory.getModel("res/models/portal/walls.obj", new LambartMaterial(wallstexture));
			scene.add(redlevel);

			Vector3f roomscale = new Vector3f(24,30,24);
			Vector3f probepos = new Vector3f(0,15,0);
			CubeMap envmap = new CubeMap("environemntmap", 256, PixelFormat.SRGB8, probepos, roomscale);
			EnvironmentProbe probe = new EnvironmentProbe(envmap);
			scene.add(probe);


			Texture normalmap = TextureManager.newTexture("res\\textures\\water1-n.jpg", PixelComponents.RGB, PixelFormat.RGB8);
			DeferredMaterial deferredmaterail = deferredRenderer.getNewMaterial();
			deferredmaterail.setAlbedoColor(0,0,0,1);
			deferredmaterail.setRefractivity(0);
			deferredmaterail.setEnvironmentMap(envmap);
			deferredmaterail.setReflectivity(1);
			deferredmaterail.setTextureRepeat(5,5);
			deferredmaterail.setNormalMap(normalmap);
			Model3D plane = ModelFactory.getModel("res/models/plane.obj", deferredmaterail);
			plane.setPosition(0,1,0);
			scene.add(plane);


			cube = ModelFactory.getModel("res/models/cube.obj", new ChromeMaterial(envmap));
			cube.setScale(4,4,4);
			cube.setPosition(probepos);
			scene.add(cube);

		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load scene!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(100);
		camera.setPosition(6, 5, 8);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/500f;
		Quaternion cuberotation = new Quaternion();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			cuberotation.rotate(0.12f, 0.09f, .1f);
			cube.setRotation(cuberotation);

			Renderer.setRenderer(forwardrenderer);
			Renderer.update();
			Renderer.setRenderer(deferredRenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredRenderer.debugGBuffer();
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

