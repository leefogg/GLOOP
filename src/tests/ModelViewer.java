package tests;

import engine.graphics.models.Skybox;
import engine.graphics.rendering.*;
import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.rendering.DeferredRenderer;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.rendering.DeferredMaterial;
import engine.graphics.shading.materials.SingleColorMaterial;
import engine.graphics.textures.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class ModelViewer {
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

		DeferredRenderer deferredrenderer = null;
		ForwardRenderer forwardrenderer = null;
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
			forwardrenderer = Renderer.getForwardRenderer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = deferredrenderer.getScene();
		forwardrenderer.setScene(scene);

		PointLight light1 = new PointLight();
		light1.linearAttenuation = 0.000f;
		light1.quadraticAttenuation = 0.00f;
		scene.add(light1);

		scene.getAmbientlight().setColor(0.5f, .5f, 0.5f);

		Model3D model = null;
		Model3D lightmodel = null;
		try {
			String[] skyboxpaths = new String[] {
					"res\\textures\\skyboxes\\stormydays\\right.png",
					"res\\textures\\skyboxes\\stormydays\\left.png",
					"res\\textures\\skyboxes\\stormydays\\up.png",
					"res\\textures\\skyboxes\\stormydays\\down.png",
					"res\\textures\\skyboxes\\stormydays\\back.png",
					"res\\textures\\skyboxes\\stormydays\\front.png",
			};
			CubeMap cubemap = new CubeMap("cubemap", skyboxpaths, PixelComponents.RGBA, PixelFormat.SRGB8);
			Skybox skybox = new Skybox(cubemap);
			scene.add(skybox);

			DeferredMaterial material = deferredrenderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res\\models\\Metal_Water_Tank\\textures\\albedo.png", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoTexture(albedo);
			Texture normals = TextureManager.newTexture("res\\models\\Metal_Water_Tank\\textures\\normals.png", PixelComponents.RGB, PixelFormat.RGB8);
			material.setNormalTexture(normals);
			material.setEnvironmentTexture(cubemap);
			material.setReflectivity(0.6f);
			model = new Model3D("res\\models\\Metal_water_tank\\Water_Tank_BI.obj", material);
			model.setScale(2,2,2);
			scene.add(model);

			material = deferredrenderer.getNewMaterial();
			albedo = TextureManager.newTexture("res\\models\\Metal_Water_Tank\\textures\\floor_ao.png", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoTexture(albedo);
			material.setEnvironmentTexture(cubemap);
			model = new Model3D("res\\models\\Metal_water_tank\\floor.obj", material);
			model.setScale(2,2,2);
			scene.add(model);

			SingleColorMaterial fullbright = new SingleColorMaterial();
			lightmodel = new Model3D("res\\models\\sphere.obj", fullbright);
			scene.add(lightmodel);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			e.printStackTrace(System.err);
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setzfar(100);
		camera.setPosition(-1,7,19);
		Renderer.setCamera(camera);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		Vector3f lightposition = new Vector3f();
		while(isrunning) {
			Renderer.updateTimeDelta();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			lightposition.set(10,35, 0);
			light1.setPosition(lightposition);
			lightmodel.setPosition(lightposition);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredrenderer.renderAttachments(8);
			Renderer.swapBuffers();

			Viewport.update();
			Viewport.setTitle("ModelViewer " + Viewport.getCurrentFrameRate() + "Hz");

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
