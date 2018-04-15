package tests;

import engine.graphics.Settings;
import engine.graphics.models.Skybox;
import engine.graphics.rendering.*;
import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.rendering.DeferredRenderer;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.rendering.DeferredMaterial;
import engine.graphics.shading.materials.SingleColorMaterial;
import engine.graphics.shading.posteffects.BloomPostEffect;
import engine.graphics.shading.posteffects.ToneMappingPostEffect;
import engine.graphics.textures.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public class HDRTest {
	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
//			Viewport.setVSyncEnabled(false);
//			Viewport.limitFrameRate(false);
			Viewport.show();
			Settings.EnableHDR = true;
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
		light1.quadraticAttenuation = 0.00f;
		scene.add(light1);
		for (int i=0; i<64; i++) {
			PointLight light = new PointLight();
			light.setPosition(0,0,-25);
			light.quadraticAttenuation = 0.01f;
			scene.add(light);
		}

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
			material.setAlbedoTexture(albedo);
			Texture normals = TextureManager.newTexture("res\\models\\SOMA\\ark\\normals.bmp", PixelComponents.RGB, PixelFormat.RGB8);
			material.setNormalMap(normals);
			Texture specular = TextureManager.newTexture("res\\models\\SOMA\\ark\\specular.png", PixelComponents.R, PixelFormat.R8);
			material.setSpecularMap(specular);
			material.setRoughness(0.2f);
			material.setReflectivity(0.1f);
			material.setEnvironmentMap(cubemap);

			Model3D model = new Model3D("res\\models\\SOMA\\ark\\model.obj", material);
			//model.setScale(20,20,20);
			model.setPosition(10,10,10);
			scene.add(model);

			material = deferredrenderer.getNewMaterial();
			albedo = TextureManager.newTexture("res\\textures\\wood.png", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoTexture(albedo);
			Model3D tunnel = new Model3D("res\\models\\masking\\box.obj", material);
			tunnel.setScale(1, 1, 10);
			tunnel.setPosition(0, -2.5f,0);
			scene.add(tunnel);

			SingleColorMaterial fullbright = new SingleColorMaterial();
			lightmodel = new Model3D("res\\models\\sphere.obj", fullbright);
			scene.add(lightmodel);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			e.printStackTrace(System.err);
			exitCleanly(1);
		}

		ToneMappingPostEffect tonemap = null;
		try {
			tonemap = new ToneMappingPostEffect();
			Renderer.addPostEffect(tonemap);
			Renderer.enablePostEffects();
		} catch (IOException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setPosition(0,0,20);
		scene.currentCamera = camera;

		System.gc();

		boolean isrunning = true;
		float exposure = 1f;
		while(isrunning) {
			Renderer.updateTimeDelta();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			exposure += (float)Mouse.getDWheel() / 5000f;
			tonemap.setExposure(exposure);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();

			Renderer.swapBuffers();
			deferredrenderer.renderAttachments();

			Viewport.update();
			Viewport.setTitle("HDR Testing " + Viewport.getCurrentFrameRate() + "Hz");

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
