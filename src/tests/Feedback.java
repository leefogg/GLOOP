package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.texturing.*;
import GLOOP.general.math.Quaternion;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public class Feedback {
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

		DeferredRenderer deferredrenderer = null;
		ForwardRenderer forwardrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
			forwardrenderer = Renderer.getForwardRenderer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = deferredrenderer.getScene();
		forwardrenderer.setScene(scene);

		scene.getAmbientlight().setColor(1,1,1);

		Model3D glados = null;
		try {
			DeferredMaterial floormaterial = deferredrenderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res\\textures\\default.png", PixelComponents.RGB, PixelFormat.SRGB8);
			floormaterial.setAlbedoTexture(albedo);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", floormaterial);
			scene.add(floor);

			Texture lastframe = deferredrenderer.getTexture();

			DeferredMaterial cubematerial = deferredrenderer.getNewMaterial();
			albedo = TextureManager.newTexture("res\\textures\\glados_head.png", PixelComponents.RGB, PixelFormat.SRGB8);
			cubematerial.setAlbedoTexture(albedo);
			glados = ModelFactory.getModel("res/models/glados.obj", cubematerial);
			glados.setPosition(-20,10,0);
			glados.setScale(20,20,20);
			scene.add(glados);

			cubematerial = deferredrenderer.getNewMaterial();
			cubematerial.setAlbedoTexture(lastframe);
			Model3D plane = ModelFactory.getModel("res/models/plane.obj", cubematerial);
			plane.setPosition(20,5,0);
			Quaternion rotation = new Quaternion();
			rotation.rotate(-90,0,0);
			plane.setRotation(rotation);
			plane.setScale(0.2f,0.2f,0.2f);
			scene.add(plane);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(1000);
		camera.setPosition(-1,7,19);

		Renderer.setVoidColor(0,0,0.25f);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		Quaternion rotation = new Quaternion();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			rotation.rotate(0,0.5f, 0);
			glados.setRotation(rotation);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			Renderer.swapBuffers();

			Viewport.setTitle("Recursive feedback loop @ " + Viewport.getCurrentFrameRate() + "Hz");

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
