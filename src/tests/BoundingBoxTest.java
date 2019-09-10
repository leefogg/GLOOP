package tests;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.ForwardRenderer;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Scene;
import gloop.graphics.rendering.Viewport;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.materials.LambartMaterial;
import gloop.graphics.rendering.shading.materials.SingleColorMaterial;
import gloop.graphics.rendering.texturing.*;
import gloop.general.math.Quaternion;
import gloop.physics.data.AABB;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public final class BoundingBoxTest {
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

		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Scene scene = renderer.getScene();

		PointLight light1 = new PointLight();
		light1.setPosition(0,1,0);
		light1.quadraticAttenuation = 0.01f;
		scene.add(light1);

		Model3D box = null, charizard = null;
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			scene.add(floor);

			albedo = TextureManager.newTexture("res/textures/charizard.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			charizard = ModelFactory.getModel("res/models/charizard.obj", new LambartMaterial(albedo));
			scene.add(charizard);

			box = ModelFactory.getModel("res/models/frame.obj", new SingleColorMaterial());
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
		camera.setzfar(100);
		camera.setPosition(-1,7,19);

		System.gc();

		AABB charboundingbox = new AABB(0,0,0,0,0,0);

		boolean isrunning = true;
		float sincos = 0, step = (float)Math.PI/300f;
		Quaternion rotation = new Quaternion();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			Renderer.setRenderer(renderer);
			Renderer.render();

			sincos += step;

			rotation.rotate(0, sincos*20, 0);

			charizard.setRotation(rotation);
			charizard.setScale(1f + (float)Math.cos(sincos)*0.5f, 1 + (float)Math.cos(sincos)*0.5f, 1 + (float)Math.cos(sincos)*0.5f);

			charizard.getBoundingBox(charboundingbox);
			box.setScale(charboundingbox.width, charboundingbox.height, charboundingbox.depth);
			box.setPosition(charboundingbox.getCentre().x, charboundingbox.getCentre().y, charboundingbox.getCentre().z);
			box.setRotation(rotation);
			box.render();

			AABB.createFromRotated(rotation, charboundingbox);
			box.setScale(charboundingbox.width, charboundingbox.height, charboundingbox.depth);
			box.setPosition(charboundingbox.getCentre().x, charboundingbox.getCentre().y, charboundingbox.getCentre().z);
			rotation.toIdentity();
			box.setRotation(rotation);
			box.render();

			Renderer.swapBuffers();

			Viewport.setTitle("Bounding Box Tests @ " + Viewport.getCurrentFrameRate() + "Hz");

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
