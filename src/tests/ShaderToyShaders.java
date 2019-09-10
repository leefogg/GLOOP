package tests;

import gloop.graphics.rendering.ForwardRenderer;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Scene;
import gloop.graphics.rendering.Viewport;
import gloop.graphics.cameras.DebugCamera;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import gloop.graphics.data.models.Model2D;
import gloop.graphics.rendering.shading.materials.ShaderToyMaterial;

public final class ShaderToyShaders {
	public static void main(String[] args) {
		try {
			Viewport.create(640, 480, "Engine Testing");
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ForwardRenderer forwardrenderer = Renderer.getForwardRenderer();

		DebugCamera camera = new DebugCamera();
		forwardrenderer.getScene().setGameCamera(camera);

		Scene scene = forwardrenderer.getScene();
		try {
			int halfwidth = Viewport.getWidth()/2;
			int halfheight =  Viewport.getHeight()/2;

			Model2D ui = new Model2D(0,0, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\ShaderToy\\Divide.frag"));
			scene.add(ui);

			ui = new Model2D(halfwidth, 0, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\ShaderToy\\Flame.frag"));
			scene.add(ui);

			ui = new Model2D(0, halfheight, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\ShaderToy\\FlowNoise.frag"));
			scene.add(ui);

			ui = new Model2D(halfwidth, halfheight, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\ShaderToy\\ImplicitSurfaces.frag"));
			scene.add(ui);
		} catch (Exception e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		System.gc();

		while(true) {
			Renderer.setRenderer(forwardrenderer);
			// Render models
			Renderer.render();
			Renderer.swapBuffers();

			Viewport.update();
			Viewport.setTitle("Development Engine " + Viewport.getCurrentFrameRate() + "Hz");

			if (Display.isCloseRequested())
				break;
			if (!Mouse.isGrabbed() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				break;
		}

		exitCleanly(0);
	}

	static void exitCleanly(int errorcode) {
		Viewport.close();
		System.exit(errorcode);
	}
}

