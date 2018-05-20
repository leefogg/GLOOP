package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.models.VertexArray;
import engine.graphics.rendering.ForwardRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.textures.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.util.Random;

public final class CullingTest {
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
		light1.quadraticAttenuation = 0.001f;
		scene.add(light1);


		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			floor.setIsOccuder(true);
			floor.setScale(2,2,2);
			scene.add(floor);

			albedo = TextureManager.newTexture("res/textures/default.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D walls = ModelFactory.getModel("res/models/circular walls.obj", new LambartMaterial(albedo));
			walls.setScale(4,4,4);
			walls.setIsOccuder(true);
			scene.add(walls);

			Model3D cube = ModelFactory.getModel("res/models/cube.obj", new LambartMaterial(albedo));
			Random r = new Random();
			for (int i=0; i<400; i++) {
				Model3D newcube = cube.clone();
				newcube.setPosition(r.nextFloat() * 200-100, 1.5f, r.nextFloat()*200-100);
				newcube.setScale(3,3,3);
				scene.add(newcube);
			}
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		DebugCamera debugcamera = new DebugCamera();
		DebugCamera gamecamera = new DebugCamera();
		scene.setDebugCamera(debugcamera);
		scene.setGameCamera(gamecamera);
		debugcamera.setzfar(300);
		gamecamera.setzfar(300);
		gamecamera.setPosition(-1,7,19);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		boolean usedebugcamera = false;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			if (usedebugcamera)
				debugcamera.update(delta, timescaler);
			else
				gamecamera.update(delta, timescaler);

			if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
				usedebugcamera = !usedebugcamera;
				Renderer.useDebugCamera(usedebugcamera);
			}

			//sincos += step * timescaler;
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);

			Renderer.setRenderer(renderer);
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
