package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.rendering.ForwardRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.shading.materials.SingleColorMaterial;
import engine.graphics.textures.*;
import engine.math.Quaternion;
import engine.physics.data.AABB;
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
		AABB charboundingbox = null;
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			scene.add(floor);

			albedo = TextureManager.newTexture("res/textures/charizard.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			charizard = ModelFactory.getModel("res/models/charizard.obj", new LambartMaterial(albedo));
			charboundingbox = charizard.getBoundingBox();
			scene.add(charizard);

			box = ModelFactory.getModel("res/models/frame.obj", new SingleColorMaterial());
			scene.add(box);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setzfar(100);
		camera.setPosition(-1,7,19);
		scene.currentCamera = camera;

		System.gc();

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

			rotation.toIdentity();
			rotation.rotate(0, sincos*20, 0);
			charizard.setRotation(rotation);
			charizard.setPosition((float)Math.cos(sincos)*20, 0,0);
			box.setScale(charboundingbox.width, charboundingbox.height, charboundingbox.depth);
			box.setPosition(charboundingbox.getCentre().x+(float)Math.cos(sincos)*20, charboundingbox.getCentre().y, charboundingbox.getCentre().z);
			box.setRotation(rotation);
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
