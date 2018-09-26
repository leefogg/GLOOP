package tests;

import GLOOP.graphics.rendering.ForwardRenderer;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.RenderMode;
import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.materials.FullBrightMaterial;
import GLOOP.graphics.rendering.shading.materials.FullBrightShader;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.Random;

public class RenderModes {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		Random r = new Random();

		float[] positions = new float[3*1000];
		for (int i=0; i<positions.length; i++)
			positions[i] = (r.nextFloat() - 0.5f) * 100f;
		VertexArray pointsbuffer = new VertexArray("points");
		pointsbuffer.storeVertcies(positions);

		FullBrightShader shader = null;
		try {
			shader = new FullBrightShader();
		} catch (Exception e) {
			System.err.println("Failed to load shader!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		Model3D model1 = null;
		try {
			model1 = new Model3D(pointsbuffer, new FullBrightMaterial(shader));
		} catch (ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		ForwardRenderer forwardrenderer = Renderer.getForwardRenderer();
		DebugCamera camera = new DebugCamera();
		forwardrenderer.getScene().setGameCamera(camera);
		forwardrenderer.getScene().setDebugCamera(camera);
		camera.setPosition(2.066602f, 1.0899944f, 115.840645f);

		System.gc();

		boolean isrunning = true;
		int frame = 0;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			frame++;
			switch(frame/Viewport.getTargetFrameRate() % 4) {
				case 0:
					pointsbuffer.setRenderingMode(RenderMode.Points);
					break;
				case 1:
					pointsbuffer.setRenderingMode(RenderMode.Lines);
					break;
				case 2:
					pointsbuffer.setRenderingMode(RenderMode.LineLoop);
					break;
				case 3:
					pointsbuffer.setRenderingMode(RenderMode.TriangleStrip);
			}

			Renderer.setRenderer(forwardrenderer);
			model1.render();
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
