package tests;

import gloop.general.exceptions.UnsupportedException;
import gloop.general.math.Quaternion;
import gloop.graphics.cameras.DebugCamera;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.*;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.lights.PointLight;
import gloop.graphics.rendering.shading.materials.ChromeMaterial;
import gloop.graphics.rendering.shading.materials.LambartMaterial;
import gloop.graphics.rendering.shading.materials.SingleColorMaterial;
import gloop.graphics.rendering.texturing.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.util.Random;

public final class DynamicCubeMapTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1920, 1080, "Engine Testing");
//			Viewport.setVSyncEnabled(false);
//			Viewport.limitFrameRate(false);
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		ForwardRenderer forwardrenderer = Renderer.getForwardRenderer();
		DeferredRenderer deferredRenderer;
		try {
			 deferredRenderer = Renderer.getDeferedRenderer();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Vector3f[] colours = new Vector3f[6];
		colours[0] = new Vector3f(1, 0, 0);
		colours[1] = new Vector3f(0, 1, 0);
		colours[2] = new Vector3f(1, 1, 0);
		colours[3] = new Vector3f(0, 0, 1);
		colours[4] = new Vector3f(1, 0, 1);
		colours[5] = new Vector3f(0, 1, 1);

		Scene scene = forwardrenderer.getScene();
		deferredRenderer.setScene(scene);

		PointLight light = new PointLight();
		light.setPosition(1,12,1);
		light.quadraticAttenuation = 0.0001f;
		scene.add(light);

		Model3D cube = null;
		Vector3f roomScale = new Vector3f(50, 50, 50);
		Vector3f halfRoomScale = new Vector3f(roomScale);
		halfRoomScale.scale(0.5f);
		Vector2f wallTextureRepeat = new Vector2f(6,6);
		LambartMaterial[] wallMaterials = new LambartMaterial[4];
		EnvironmentProbe probe = null;
		Random r = new Random();
		try {
			Texture wallstexture = TextureManager.newTexture("res\\textures\\panel.png", PixelComponents.RGB, PixelFormat.SRGB8);
			Model3D floor = ModelFactory.getModel("res/models/portal/floor.obj", new LambartMaterial(wallstexture));
			scene.add(floor);

			{
				for (int i=0; i<wallMaterials.length; i++) {
					wallMaterials[i] = new LambartMaterial(wallstexture);
					wallMaterials[i].setTextureRepeat(wallTextureRepeat);
					wallMaterials[i].setTextureTint(colours[r.nextInt(colours.length)]);
				}
				Quaternion rotation = new Quaternion();

				// Left
				Model3D wall = ModelFactory.getModel("res/models/primitives/plane.obj", wallMaterials[0]);
				wall.setScale(roomScale);
				rotation.rotate(0, 0, -90);
				wall.setPosition(halfRoomScale.x, halfRoomScale.y, 0);
				wall.setRotation(rotation);
				scene.add(wall);

				// Right
				wall = ModelFactory.getModel("res/models/primitives/plane.obj", wallMaterials[1]);
				wall.setScale(roomScale);
				rotation.toIdentity();
				rotation.rotate(0, 0, 90);
				wall.setPosition(-halfRoomScale.x, halfRoomScale.y, 0);
				wall.setRotation(rotation);
				scene.add(wall);

				// Forward
				wall = ModelFactory.getModel("res/models/primitives/plane.obj", wallMaterials[2]);
				wall.setScale(roomScale);
				rotation.toIdentity();
				rotation.rotate(90, 0, 0);
				wall.setPosition(0, halfRoomScale.y, halfRoomScale.z);
				wall.setRotation(rotation);
				scene.add(wall);

				wall = ModelFactory.getModel("res/models/primitives/plane.obj", wallMaterials[3]);
				wall.setScale(roomScale);
				rotation.toIdentity();
				rotation.rotate(-90, 0, 0);
				wall.setPosition(0, halfRoomScale.y, -halfRoomScale.z);
				wall.setRotation(rotation);
				scene.add(wall);

				// Ceiling
				LambartMaterial mat = new LambartMaterial(wallstexture);
				Model3D ceiling = ModelFactory.getModel("res/models/primitives/plane.obj", mat);
				rotation.toIdentity();
				rotation.rotate(0,0,180);
				ceiling.setRotation(rotation);
				ceiling.setScale(roomScale);
				ceiling.setPosition(0, roomScale.y, 0);
				scene.add(ceiling);
			}



			Vector3f probepos = new Vector3f(0, halfRoomScale.y,0);
			CubeMap envmap = new CubeMap("environemntmap", 256, PixelFormat.SRGB8, probepos, roomScale);
			probe = new EnvironmentProbe(envmap);
			scene.add(probe);


			Texture normalmap = TextureManager.newTexture("res\\textures\\153_norm.jpg", PixelComponents.RGB, PixelFormat.RGB8);
			DeferredMaterial deferredmaterail = deferredRenderer.getNewMaterial();
			deferredmaterail.setAlbedoColor(0,0,0,1);
			deferredmaterail.setRefractivity(0);
			deferredmaterail.setEnvironmentMap(envmap);
			deferredmaterail.setReflectivity(1);
			deferredmaterail.setTextureRepeat(20,20);
			deferredmaterail.setNormalMap(normalmap);
			deferredmaterail.setNormalMapScale(0.04f);
			Model3D plane = ModelFactory.getModel("res/models/plane.obj", deferredmaterail);
			plane.setPosition(0,1,0);
			scene.add(plane);


			cube = ModelFactory.getModel("res/models/cube.obj", new ChromeMaterial(envmap));
			cube.setScale(8,8,8);
			cube.setPosition(probepos);
			scene.add(cube);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load scene!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(120);
		camera.setPosition(0,halfRoomScale.y,20);

		System.gc();

		boolean isrunning = true;
		Quaternion cuberotation = new Quaternion();
		Keyboard.enableRepeatEvents(false);
		float lastChangeTime = 0;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			cuberotation.rotate(0.12f, 0.09f, .1f);
			cube.setRotation(cuberotation);


			Renderer.setRenderer(forwardrenderer);
			if (Viewport.getElapsedSeconds() > lastChangeTime + 5) {
				lastChangeTime = Viewport.getElapsedSeconds();

				for (LambartMaterial mat : wallMaterials) {
					mat.setTextureTint(colours[r.nextInt(colours.length)]);
				}

				probe.setFramesUntilRenew(0);
			}
			Renderer.update();
			Renderer.setRenderer(deferredRenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredRenderer.debugGBuffer();
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

