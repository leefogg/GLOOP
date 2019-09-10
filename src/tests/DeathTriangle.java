package tests;

import gloop.graphics.data.DataConversion;
import gloop.graphics.data.models.GLArrayType;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.VertexArray;
import gloop.graphics.data.models.VertexBuffer;
import gloop.graphics.rendering.ForwardRenderer;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.Scene;
import gloop.graphics.rendering.Viewport;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.IOException;
import java.nio.FloatBuffer;

public class DeathTriangle {
	public static void main(String[] args) {
		try {
			Viewport.create(640, 480, "Engine Testing");
			Viewport.setVSyncEnabled(false);
			Viewport.limitFrameRate(false);
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}
		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Scene scene = renderer.getScene();


		float[] verts = new float[] {
				-0.5f, -0.5f, 0,
				0.5f, -0.5f, 0,
				0.0f,  0.5f, 0,
		};
		float[] colors = new float[] {
			1,0,0,
			0,1,0,
			0,0,1
		};
		FloatBuffer colorbuffer = DataConversion.toGLBuffer(colors);

		VertexBuffer colormem = new VertexBuffer(GLArrayType.Array);
		colormem.store(colorbuffer);
		VertexArray meshdata = new VertexArray("triangle");
		meshdata.storeVertcies(verts);
		meshdata.bindAttribute(colormem, 1, 3);

		Renderer.enableFaceCulling(false);
		try {
			VertexColorShader shader = new VertexColorShader();
			VertexColorMaterial material = new VertexColorMaterial(shader);
			Model3D mesh = new Model3D(meshdata, material);
			scene.add(mesh);

			boolean isrunning = true;
			while(isrunning) {
				Viewport.update();

				Renderer.setRenderer(renderer);
				Renderer.render();

				Renderer.swapBuffers();

				Viewport.setTitle("TRIANGLE OF DEATH @ " + Viewport.getCurrentFrameRate() + "FPS");

				if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Display.isCloseRequested())
					isrunning = false;
			}
		} catch (IOException e) {

		}

		meshdata.dispose();
		Viewport.close();
		System.exit(0);
	}
}
