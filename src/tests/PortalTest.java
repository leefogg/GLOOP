package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.general.math.Quaternion;
import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.cameras.PerspectiveCamera;
import GLOOP.graphics.data.models.Model2D;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.materials.FullBrightMaterial;
import GLOOP.graphics.rendering.shading.materials.LambartMaterial;
import GLOOP.graphics.rendering.shading.materials.SingleColorMaterial;
import GLOOP.graphics.rendering.shading.posteffects.PostProcess;
import GLOOP.graphics.rendering.shading.posteffects.PostProcessor;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class PortalTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1920, 1080, "Portal Testing");
//			Viewport.setVSyncEnabled(false);
//			Viewport.limitFrameRate(false);
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ForwardRenderer renderer = Renderer.getForwardRenderer();
		Scene redscene = renderer.getScene();
		Scene greenscene = new Scene();
		DebugCamera camera = new DebugCamera();
		redscene.setDebugCamera(camera);
		redscene.setGameCamera(camera);
		camera.setPosition(0,4,9);

		Camera othercam = greenscene.getGameCamera();
		FrameBuffer othercamvrendertarget = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{ PixelFormat.SRGB8 }, true, false);
		Texture othercamview = othercamvrendertarget.getColorTexture(0);

		Vector3f portalsoffset = new Vector3f(-10f, 0, -24.138f);
		Model3D portal = null;
		Model2D portaloverlay = null;
		Model3D player = null;
		try {
			Texture wallstexture = TextureManager.newTexture("res\\textures\\portal\\concrete_modular_wall001_gradient00.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			Texture floortexture = TextureManager.newTexture("res\\textures\\portal\\concrete_modular_floor001c.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D floor = ModelFactory.getModel("res/models/portal/floor.obj", new LambartMaterial(floortexture));

			{
				Model3D redlevel = ModelFactory.getModel("res/models/portal/walls.obj", new LambartMaterial(wallstexture));
				redscene.add(redlevel);

				PointLight redlevellight = new PointLight();
				redlevellight.setPosition(1,1,1);
				redlevellight.quadraticAttenuation = 0.01f;
				redscene.add(redlevellight);
				redscene.add(floor);

				portal = ModelFactory.getModel("res/models/portal/portal.obj", new LambartMaterial(wallstexture));
				Quaternion rotation = new Quaternion();
				rotation.rotate(0,180,0);
				portal.setRotation(rotation);
				portal.setPosition(-7.498f,2.411f,-11.988f);

				portaloverlay = new Model2D(0,0,Viewport.getWidth(), Viewport.getHeight());
				((FullBrightMaterial)portaloverlay.getMaterial()).setAlbedoTexture(othercamview);
			}

			{

				Model3D greenlevel = ModelFactory.getModel("res/models/portal/walls.obj", new LambartMaterial(wallstexture));
				greenscene.add(greenlevel);

				PointLight greenlevellight = new PointLight();
				greenlevellight.setPosition(1,1,1);
				greenlevellight.quadraticAttenuation = 0.01f;
				greenscene.add(greenlevellight);
				greenscene.add(floor);

				player = ModelFactory.getModel("res\\models\\primitives\\cylinder.obj", new SingleColorMaterial(Color.red));
				player.setScale(1.512f, 3.5f, 1.5f);
				greenscene.add(player);
			}

		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		Vector3f temp = new Vector3f();
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			//sincos += step * timescaler;
			// Copy the players position and rotation to othe camera
			camera.getRotation(temp);
			othercam.setRotation(temp);
			camera.getPosition(temp);
			temp.x -= portalsoffset.x;
			temp.y -= portalsoffset.y;
			temp.z -= portalsoffset.z;
			othercam.setPosition(temp);
			// Copy players position to player model
			camera.getPosition(temp);
			temp.y -= 2f;
			player.setPosition(temp);

			// Render other scene (throgh portal)
			Renderer.enableFaceCulling(true);
			Renderer.setRenderer(renderer);
			renderer.setScene(greenscene);
			othercamvrendertarget.bind();
			Renderer.clear(true, true, false);
			Renderer.render();

			// Render current player scene with portal
			Renderer.popFaceCullingEnabledState();
			Renderer.setRenderer(renderer);
			Renderer.setStencilBufferState(Condition.Always, 1, 0xFF);
			renderer.setScene(redscene);
			Renderer.render();
			Renderer.popStencilBufferState();
			Renderer.enableDepthBufferWriting(false);
			Renderer.enableColorBufferWriting(false, false, false, false);
			Renderer.setStencilBufferState(Condition.Always, 2, 0xFF);
			portal.render();
			Renderer.popStencilBufferState();
			Renderer.popColorBufferWritingState();
			Renderer.popDepthBufferWritingState();
			Renderer.setStencilBufferState(Condition.Equals, 2, 0xFF);
			portaloverlay.render();
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
