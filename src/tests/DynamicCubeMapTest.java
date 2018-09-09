package tests;

import engine.general.exceptions.UnsupportedException;
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
		DeferredRenderer deferredRenderer;
		try {
			 deferredRenderer = Renderer.getDeferedRenderer();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Scene scene = forwardrenderer.getScene();
		deferredRenderer.setScene(scene);

		scene.getAmbientlight().setColor(1,1,1);

		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\kitten.png", PixelComponents.RGBA, PixelFormat.SRGBA8);
			albedo.setFilteringMode(TextureFilter.Nearest);
			Model3D house = ModelFactory.getModel("res\\models\\insideout box.obj", new FullBrightMaterial(albedo));
			Vector3f roomscale = new Vector3f(20,20,20);
			house.setScale(roomscale);
			scene.add(house);

			Vector3f probepos = new Vector3f(0,0,0);
			CubeMap envmap = new CubeMap("environemntmap", 128, PixelFormat.SRGB8, probepos, roomscale);
			EnvironmentProbe probe = new EnvironmentProbe(envmap);
			scene.add(probe);

			Texture normalmap = TextureManager.newTexture("res\\textures\\water1-n.jpg", PixelComponents.RGB, PixelFormat.RGB8);
			DeferredMaterial deferredmaterail = deferredRenderer.getNewMaterial();
			deferredmaterail.setAlbedoColor(0,0,0,1);
			deferredmaterail.setRefractivity(1);
			deferredmaterail.setEnvironmentMap(envmap);
			deferredmaterail.setReflectivity(0);
			deferredmaterail.setTextureRepeat(10,10);
			deferredmaterail.setNormalMap(normalmap);
			Model3D plane = ModelFactory.getModel("res/models/plane.obj", deferredmaterail);
			plane.setPosition(0,-1,0);
			scene.add(plane);

			normalmap = TextureManager.newTexture("res\\textures\\6624-normal.jpg", PixelComponents.RGB, PixelFormat.RGB8);
			deferredmaterail = deferredRenderer.getNewMaterial();
			deferredmaterail.setAlbedoColor(0,0,0,1);
			deferredmaterail.setRefractivity(1);
			deferredmaterail.setEnvironmentMap(envmap);
			deferredmaterail.setReflectivity(0);
			deferredmaterail.setNormalMap(normalmap);
			deferredmaterail.setTextureRepeat(5,5);
			Model3D sphere = ModelFactory.getModel("res/models/sphere.obj", new ChromeMaterial(envmap));
			sphere.setScale(4,4,4);
			scene.add(sphere);

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
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;

			Renderer.setRenderer(forwardrenderer);
			Renderer.update();
			Renderer.setRenderer(deferredRenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			Renderer.swapBuffers();
			deferredRenderer.renderAttachments();

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

