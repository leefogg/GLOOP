package tests;

import engine.graphics.Settings;
import engine.graphics.rendering.*;
import engine.graphics.rendering.DeferredRenderer;
import engine.graphics.textures.*;
import engine.math.Quaternion;
import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.Skybox;
import engine.graphics.rendering.DeferredMaterial;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class ReflectionRefraction {
	public static void main(String[] args) {
		try {
			Settings.EnableChromaticAberration = true;
			Viewport.create(1280, 720, "Reflection and Refraction");
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}


		DeferredRenderer deferredrenderer = null;
		ForwardRenderer forwardrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
			forwardrenderer = Renderer.getForwardRenderer();
		} catch (Exception e) {
			e.printStackTrace();
			exitCleanly(1);
		}
		Scene scene = deferredrenderer.getScene();
		scene.getAmbientlight().setColor(1,1,1);
		forwardrenderer.setScene(scene);

		Model3D
		model1 = null,
		model2 = null,
		floor = null;
		Skybox skybox = null;
		DeferredMaterial refractiveballmaterial = null;
		try {
			String[] skyboxpaths = new String[] {
					"res\\textures\\skyboxes\\NatureLab\\right.png",
					"res\\textures\\skyboxes\\NatureLab\\left.png",
					"res\\textures\\skyboxes\\NatureLab\\up.png",
					"res\\textures\\skyboxes\\NatureLab\\down.png",
					"res\\textures\\skyboxes\\NatureLab\\back.png",
					"res\\textures\\skyboxes\\NatureLab\\front.png",
			};
			CubeMap cubemaptexture = new CubeMap("cubemap", skyboxpaths, PixelComponents.RGBA, PixelFormat.SRGB8);
			skybox = new Skybox(cubemaptexture);


			DeferredMaterial material = deferredrenderer.getNewMaterial();
			material.setDiffuseColor(0,0,0,1);
			material.setEnvironmentTexture(cubemaptexture);
			material.setRefractivity(0);
			material.setReflectivity(1);
			material.setDiffuseColor(0,0,0,1);
			model1 = new Model3D("res\\models\\teapot.obj", material);
			model1.setScale(5,5,5);
			model1.setPosition(-4f, 0, -5f);

			refractiveballmaterial = deferredrenderer.getNewMaterial();
			refractiveballmaterial.setDiffuseColor(0,0,0,1);
			refractiveballmaterial.setRefractivity(1);
			refractiveballmaterial.setEnvironmentTexture(cubemaptexture);
			Model3D sphere = new Model3D("res\\models\\sphere.obj", refractiveballmaterial);
			sphere.setPosition(0, 1, 5);
			scene.add(sphere);

			model2 = new Model3D("res\\models\\bunny.obj", refractiveballmaterial);
			model2.setScale(5,5,5);
			model2.setPosition(4f, 0f, -5f);


			material = deferredrenderer.getNewMaterial();
			material.setDiffuseColor(0,0,0,1);
			Texture normalmap = TextureManager.newTexture("res\\textures\\4918-normal.jpg", PixelComponents.RGB, PixelFormat.RGB8);
			normalmap.setWrapMode(TextureWrapMode.Repeat);
			material.setTextureRepeat(4,4);
			material.setNormalTexture(normalmap);
			material.setEnvironmentTexture(cubemaptexture);
			material.setReflectivity(1f);
			floor = new Model3D("res\\models\\plane.obj", material);
			floor.setPosition(0, 0, 0);


			for (int y=0; y<10; y++) {
				for (int x=0; x<10; x++) {
					material = deferredrenderer.getNewMaterial();
					material.setDiffuseColor(0,0,0,1);
					material.setReflectivity((float)y/10f);
					material.setRefractivity((float)x/10f);
					material.setEnvironmentTexture(cubemaptexture);
					material.setRefractionIndex(1.5f);
					sphere = new Model3D("res\\models\\sphere.obj", material);
					sphere.setPosition(-7, y+1f, x-5);
					scene.add(sphere);
				}
			}

			material = deferredrenderer.getNewMaterial();
			material.setDiffuseColor(0,0,0,1);
			material.setReflectivity(1f);
			normalmap = TextureManager.newTexture("res\\textures\\Worn Temple Wall.jpg", PixelComponents.RGB, PixelFormat.RGB8);
			normalmap.setWrapMode(TextureWrapMode.Repeat);
			material.setNormalTexture(normalmap);
			material.setNormalMapScale(1f);
			material.setEnvironmentTexture(cubemaptexture);
			sphere = new Model3D("res\\models\\sphere.obj", material);
			sphere.setPosition(-5, 2, 0);
			sphere.setScale(4,4,4);
			scene.add(sphere);
		} catch (IOException e) {
			e.printStackTrace(System.err);
			exitCleanly(1);
		}
		scene.add(model1);
		scene.add(model2);
		scene.add(floor);
		scene.add(skybox);

		DebugCamera camera = new DebugCamera();
		camera.setPosition(0,2,0);
		Renderer.setCamera(camera);

		System.gc();

		boolean isrunning = true;
		double sincos = 0, step = (float)Math.PI/100f;
		Vector3f up = new Vector3f(0,1,0);
		Quaternion rotation = new Quaternion();
		while (isrunning) {
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();

			sincos += step * timescaler;
			camera.update(delta, timescaler);

			refractiveballmaterial.setRefractionIndex(1.01f + (((float)Math.sin(sincos) + 1f) / 2f));
			rotation.toIdentity();
			rotation = rotation.rotate(up, (float)sincos);
			model1.setRotation(rotation);
			model2.setRotation(rotation);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredrenderer.renderAttachments();
			Renderer.swapBuffers();
			deferredrenderer.renderAttachments();

			Viewport.update();
			Viewport.setTitle("Reflection and Refraction " + Viewport.getCurrentFrameRate() + "Hz");

			if (Display.isCloseRequested())
				isrunning = false;
			if (!Mouse.isGrabbed() && Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
				isrunning = false;

			if (Keyboard.isKeyDown(Keyboard.KEY_F5)) {
				try {
					deferredrenderer.reload();
				} catch (IOException e) {
					e.printStackTrace();
					exitCleanly(1);
				}
			}
		}

		exitCleanly(0);
	}

	static void exitCleanly(int errorcode) {
		Viewport.close();
		System.exit(errorcode);
	}
}