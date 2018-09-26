package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.Settings;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.ForwardRenderer;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Scene;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.materials.LambartMaterial;
import GLOOP.graphics.rendering.shading.materials.SingleColorMaterial;
import GLOOP.graphics.rendering.shading.posteffects.BloomPostEffect;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Random;

public final class BloomTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
//			Viewport.setVSyncEnabled(false);
//			Viewport.limitFrameRate(false);
			Viewport.show();
			Settings.EnableHDR = true;
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

		try {
			Texture albedo = TextureManager.newTexture("res\\textures\\brick.png", PixelComponents.RGB, PixelFormat.SRGB8);
			albedo.generateAnisotropicMipMaps(100);
			Model3D model1 = ModelFactory.getModel("res/models/plane.obj", new LambartMaterial(albedo));
			scene.add(model1);

			Random r = new Random();
			for(int x=-10; x<=10; x+=2) {
				SingleColorMaterial mat = new SingleColorMaterial();
				Vector3f color = new Vector3f(r.nextFloat(), r.nextFloat(), r.nextFloat());
				color.normalise();
				mat.setColor(1.2f,1.2f,1.2f);

				Model3D sphere = ModelFactory.getModel("res\\models\\sphere.obj", mat);
				sphere.setScale(2,2,2);
				sphere.setPosition(x, 1,0);
				scene.add(sphere);
			}
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load scene!");
			e.printStackTrace();
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
		}

		try {
			BloomPostEffect bloompost = new BloomPostEffect();
			bloompost.setNumberOfPasses(5);
			Renderer.addPostEffect(bloompost);
			Renderer.enablePostEffects();
		} catch (IOException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(100);
		camera.setPosition(-1,7,19);

		System.gc();

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
