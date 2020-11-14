package tests;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.Settings;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.data.models.Skybox;
import gloop.graphics.rendering.*;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.materials.SingleColorMaterial;
import gloop.graphics.rendering.shading.posteffects.SSAOGBufferPostEffect;
import gloop.graphics.rendering.texturing.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class AOTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
			Viewport.show();
			Settings.EnableHDR = true;
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

		try {
			DeferredMaterial material = deferredrenderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res\\models\\Summers Forest\\s2-1_016-n.png", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoMap(albedo);
			material.setReflectivity(0.2f);
			Model3D model = ModelFactory.getModel("res\\models\\Summers Forest\\Summer Forest.obj", material);

			scene.add(model);
		} catch (IOException | ShaderCompilationException e) {
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
		camera.setzfar(100);
		camera.setPosition(-1,7,19);

		float intensity = 2;
		SSAOGBufferPostEffect ssao = null;
		try {
			ssao = new SSAOGBufferPostEffect();
			ssao.setIntensity(intensity);
			Renderer.addPostEffect(ssao);
			deferredrenderer.addPostEffect(ssao);
		} catch (IOException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		System.gc();

		boolean isrunning = true;
		while(isrunning) {
			Renderer.updateTimeDelta();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				intensity += 0.1;
				System.out.println(intensity);
			} else if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
				intensity -= 0.1;
				System.out.println(intensity);
			}
			ssao.setIntensity(intensity);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredrenderer.debugGBuffer();
			Renderer.swapBuffers();

			Viewport.update();
			Viewport.setTitle("ModelViewer " + Viewport.getCurrentFrameRate() + "Hz");

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
