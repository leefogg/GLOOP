package tests;

import engine.graphics.rendering.ForwardRenderer;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.rendering.Viewport;
import engine.graphics.cameras.DebugCamera;
import engine.graphics.rendering.UI.GUIRenderer;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import engine.graphics.models.Model2D;
import engine.graphics.shading.materials.ShaderToyMaterial;

import java.util.ArrayList;

public final class ShaderToyShaders {
	public static void main(String[] args) {
		try {
			Viewport.create(640, 480, "Engine Testing");
			Viewport.show();
			Viewport.unbindMouseOnBlur(true);
			Renderer.setVoidColor(0,0,0);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ForwardRenderer forwardrenderer = Renderer.getForwardRenderer();

		DebugCamera camera = new DebugCamera();
		Renderer.setCamera(camera);

		Scene scene = forwardrenderer.getScene();
		try {
			int halfwidth = Viewport.getWidth()/2;
			int halfheight =  Viewport.getHeight()/2;

			Model2D ui = new Model2D(0,0, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\Divide.frag"));
			scene.add(ui);

			ui = new Model2D(halfwidth, 0, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\Flame.frag"));
			scene.add(ui);

			ui = new Model2D(0, halfheight, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\FlowNoise.frag"));
			scene.add(ui);

			ui = new Model2D(halfwidth, halfheight, halfwidth, halfheight);
			ui.setMaterial(new ShaderToyMaterial("res\\shaders\\Tests\\ImplicitSurfaces.frag"));
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

