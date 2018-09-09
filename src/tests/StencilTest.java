package tests;

import engine.general.exceptions.UnsupportedException;
import engine.graphics.cameras.PerspectiveCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.rendering.*;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.shading.materials.SingleColorMaterial;
import engine.graphics.textures.*;
import engine.math.Quaternion;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class StencilTest {
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

		Model3D mask = null;
		Model3D charizard = null;
		Model3D teapot = null;
		Model3D box = null;
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D floor = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			scene.add(floor);

			SingleColorMaterial whitematerial = new SingleColorMaterial();
			whitematerial.setColor(1,1,1);
			Model3D frame = ModelFactory.getModel("res/models/masking/frame.obj", whitematerial);
			frame.setScale(3,3,3);
			scene.add(frame);

			albedo = TextureManager.newTexture("res\\textures\\180.jpg", PixelComponents.RGB, PixelFormat.SRGB8);
			LambartMaterial lambartmaterial = new LambartMaterial(albedo);
			box = ModelFactory.getModel("res/models/masking/box.obj", lambartmaterial);
			box.setScale(3,3,3);
			box.setPosition(0,0.1f,0);

			mask = ModelFactory.getModel("res/models/masking/face.obj", whitematerial);
			mask.setScale(3,3,3);

			albedo = TextureManager.newTexture("res/textures/charizard.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			charizard = ModelFactory.getModel("res/models/charizard.obj", new LambartMaterial(albedo));

			teapot = ModelFactory.getModel("res/models/teapot.obj", whitematerial);
			teapot.setScale(10,10,10);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		Renderer.checkErrors();

		PerspectiveCamera camera = new PerspectiveCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);

		System.gc();

		Renderer.enableStencilTesting(true);
		Quaternion initialrotation = new Quaternion();
		Quaternion around = new Quaternion();
		Vector3f centre = new Vector3f(0,6,0);
		around.rotate(0,180,0);

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			camera.setPosition((float)Math.cos(sincos) * 20, 10, (float)Math.sin(sincos) * 20);
			camera.lookAt(centre);
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);


			Renderer.setRenderer(renderer);
			Renderer.setStencilBufferState(Condition.Always, 88, 0xFF);
			Renderer.render();
			Renderer.popStencilBufferState();

			Renderer.enableDepthBufferWriting(false);
			Renderer.enableColorBufferWriting(false, false, false, false);
			Renderer.setStencilBufferState(Condition.Always, 1, 0xFF);
			mask.setRotation(initialrotation);
			mask.render();
			Renderer.popStencilBufferState();
			mask.setRotation(around);
			Renderer.setStencilBufferState(Condition.Always, 2, 0xFF);
			mask.render();
			Renderer.popStencilBufferState();
			Renderer.popColorBufferWritingState();
			Renderer.popDepthBufferWritingState();

			Renderer.setStencilBufferState(Condition.Equals, 1, 0xFF);
			charizard.render();
			box.setRotation(initialrotation);
			box.render();
			Renderer.popStencilBufferState();
			Renderer.setStencilBufferState(Condition.Equals, 2, 0xFF);
			teapot.render();
			box.setRotation(around);
			box.render();
			Renderer.popStencilBufferState();

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
