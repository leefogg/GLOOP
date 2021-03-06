package tests;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.*;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.materials.LambartMaterial;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.texturing.*;
import gloop.general.math.Quaternion;
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
			Viewport.setVSyncEnabled(false);
			Viewport.limitFrameRate(false);
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
			RenderQueryCullingMethod cullingmethod = new RenderQueryCullingMethod();
			cullingmethod.setMinimumRequiredVertcies(0);
			Renderer.setCullingMethod(cullingmethod);
		} catch (Exception e){
			e.printStackTrace();
			exitCleanly(1);
		}

		Model3D spinner = null;
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			floor.setIsOccuder(true);
			floor.setScale(2,2,2);
			scene.add(floor);

			albedo = TextureManager.newTexture("res/textures/default.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Quaternion rotation = new Quaternion();
			for (int i=0; i<4; i++) {
				Model3D walls = ModelFactory.getModel("res/models/circular walls slice.obj", new LambartMaterial(albedo));
				walls.setScale(4, 4, 4);
				walls.setRotation(rotation);
				walls.setIsOccuder(true);
				scene.add(walls);
				rotation.rotate(0,90,0);
			}

			Material material = new LambartMaterial(albedo);
			Model3D cube = ModelFactory.getModel("res/models/primitives/cube.obj", material);
			Model3D[] models = new Model3D[] {
					cube,
					ModelFactory.getModel("res/models/primitives/cone.obj", material),
					ModelFactory.getModel("res/models/primitives/cylinder.obj", material),
					ModelFactory.getModel("res/models/primitives/pyramid.obj", material),
					ModelFactory.getModel("res/models/primitives/soccer ball.obj", material),
					ModelFactory.getModel("res/models/primitives/torus.obj", material)
			};
			rotation.toIdentity();
			Random r = new Random();
			for (int i=0; i<2000; i++) {
				Model3D newmodel = models[r.nextInt(models.length)].clone();
				newmodel.getRotation(rotation);
				rotation.rotate(r.nextFloat()*360, r.nextFloat()*360, r.nextFloat()*360);
				newmodel.setRotation(rotation);
				newmodel.setPosition(r.nextFloat() * 200-100, 0.5f, r.nextFloat()*200-100);
				newmodel.setScale(0.5f+r.nextFloat(),0.5f+r.nextFloat(),0.5f+r.nextFloat());
				scene.add(newmodel);
			}

			spinner = cube;
			spinner.setScale(40,4,4);
			spinner.setPosition(0,2,-60);
			scene.add(spinner);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera debugcamera = new DebugCamera();
		DebugCamera gamecamera = new DebugCamera();
		scene.setDebugCamera(debugcamera);
		scene.setGameCamera(gamecamera);
		debugcamera.setzfar(300);
		gamecamera.setzfar(300);
		gamecamera.setPosition(-1,7,19);
		debugcamera.setPosition(0, 70, 120f);
		debugcamera.setRotation(35,0,0);


		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		boolean usedebugcamera = false;

		light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);
		Quaternion rotation = new Quaternion();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();

			if (Keyboard.isKeyDown(Keyboard.KEY_T)) {
				usedebugcamera = !usedebugcamera;
				Renderer.useDebugCamera(usedebugcamera);
			}

			gamecamera.update(delta, timescaler);

			sincos += step * timescaler;
			spinner.getRotation(rotation);
			rotation.rotate(0,0.5f * timescaler, 0);
			spinner.setRotation(rotation);

			Renderer.update();
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
