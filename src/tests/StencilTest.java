package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
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
		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D model1 = new Model3D("res/models/plane.obj", new LambartMaterial(albedo));
			scene.add(model1);

			mask = new Model3D("res/models/plane.obj", new SingleColorMaterial());
			Quaternion rotation = new Quaternion();
			rotation.rotate(-90,0,0);
			mask.setRotation(rotation);
			mask.setPosition(0,10,8);
			mask.setScale(1/20f, 1/20f, 1/20f);

			albedo = TextureManager.newTexture("res/textures/charizard.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.setFilteringMode(TextureFilter.Linear);
			charizard = new Model3D("res/models/charizard.obj", new LambartMaterial(albedo));
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		Renderer.checkErrors();

		DebugCamera camera = new DebugCamera();
		camera.setzfar(100);
		camera.setPosition(-1,7,19);
		scene.currentCamera = camera;

		System.gc();

		Renderer.enableStencilTesting(true);

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			//sincos += step * timescaler;
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);

			Renderer.setRenderer(renderer);
			Renderer.setStencilBufferState(Condition.Always, 88, 0xFF);
			Renderer.render();
			Renderer.popStencilBufferState();
			Renderer.setStencilBufferState(Condition.Always, 1, 0xFF);
			Renderer.enableDepthBufferWriting(false);
			Renderer.enableColorBufferWriting(false, false, false, false);
			mask.render();
			Renderer.popColorBufferWritingState();
			Renderer.popDepthBufferWritingState();
			Renderer.popStencilBufferState();
			Renderer.setStencilBufferState(Condition.Equals, 1, 0xFF);
			charizard.render();
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
