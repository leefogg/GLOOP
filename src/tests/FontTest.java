package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.fonts.Font;
import engine.graphics.models.Model2D;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.rendering.ForwardRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.DecalMaterial;
import engine.graphics.shading.materials.FullBrightMaterial;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.shading.posteffects.FXAAPostEffect;
import engine.graphics.textures.*;
import engine.math.Quaternion;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class FontTest {
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


		Font font = null;
		try {
			Texture fontatlas = TextureManager.newTexture("res/fonts/arial.png", PixelComponents.RGBA, PixelFormat.RGBA8);
			font = new Font(fontatlas, "res/fonts/arial.fnt");
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
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

			sincos += step * timescaler;
			light1.setPosition((float)Math.sin(sincos)*20, 0, (float)Math.cos(sincos)*20);


			Renderer.setRenderer(renderer);
			Renderer.render();
			float fontsize = 5 + ((float)Math.cos(sincos) / 2f + 0.5f) * 950f;
			font.render(("" + fontsize).toCharArray(), 20, 600, 50);
			font.render("ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(), 50,0, fontsize, new Vector3f(1,0,1));
			font.render("abcdefghijklmnopqrstuvwxyz".toCharArray(), 50,50, fontsize);
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

