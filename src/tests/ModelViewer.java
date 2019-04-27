package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.data.models.Skybox;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.rendering.DeferredRenderer;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.DeferredMaterial;
import GLOOP.graphics.rendering.shading.materials.SingleColorMaterial;
import GLOOP.graphics.rendering.texturing.*;
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
		light1.quadraticAttenuation = 0.03f;
		scene.add(light1);

		scene.getAmbientlight().setColor(0.04f, 0.04f, .04f);

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
			Texture albedo = TextureManager.newTexture("res\\models\\SOMA\\ark\\albedo.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoMap(albedo);
			Texture normals = TextureManager.newTexture("res\\models\\SOMA\\ark\\normals.bmp", PixelComponents.RGB, PixelFormat.RGB8);
			material.setNormalMap(normals);
			Texture specular = TextureManager.newTexture("res\\models\\SOMA\\ark\\specular.png", PixelComponents.R, PixelFormat.R8);
			material.setSpecularMap(specular);
			material.setRoughness(0.2f);
			material.setEnvironmentMap(cubemap);
			material.setReflectivity(0.2f);
			Model3D model = ModelFactory.getModel("res\\models\\SOMA\\ark\\model.obj", material);
			model.setScale(20,20,20);
			scene.add(model);

			SingleColorMaterial fullbright = new SingleColorMaterial();
			lightmodel = ModelFactory.getModel("res\\models\\sphere.obj", fullbright);
			scene.add(lightmodel);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			e.printStackTrace(System.err);
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setPosition(-1,7,19);

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
			lightposition.set((float)Math.sin(sincos)*22,5, (float)Math.cos(sincos)*22);
			light1.setPosition(lightposition);
			lightmodel.setPosition(lightposition);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			deferredrenderer.debugGBuffer();
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
