package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.rendering.*;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.shading.materials.DecalMaterial;
import engine.graphics.shading.materials.LambartMaterial;
import engine.graphics.shading.posteffects.FXAAPostEffect;
import engine.graphics.textures.*;
import engine.math.Quaternion;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

public final class SOMA {
	public static void main(String[] args) {
		try {
			Viewport.create(1920, 1080, "Engine Testing");
			Viewport.show();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(1);
		}

		DeferredRenderer renderer = null;
		try {
			renderer = Renderer.getDeferedRenderer();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		Scene scene = renderer.getScene();

		PointLight whitelight = new PointLight();
		whitelight.setPosition(0, 4,0);
		whitelight.quadraticAttenuation = 0.01f;
		scene.add(whitelight);
		PointLight greenlight = new PointLight();
		greenlight.setColor(0,1,0);
		greenlight.setPosition(10,4,10);
		greenlight.quadraticAttenuation = 0.01f;
		scene.add(greenlight);
		PointLight bluelight = new PointLight();
		bluelight.setColor(0,0,1);
		bluelight.setPosition(-10,4,10);
		bluelight.quadraticAttenuation = 0.01f;
		scene.add(bluelight);
		PointLight redlight = new PointLight();
		redlight.setColor(1,0,0);
		redlight.setPosition(0,4,-10);
		redlight.quadraticAttenuation = 0.01f;
		scene.add(redlight);


		try {
			Texture albedomap = TextureManager.newTexture("res\\textures\\SOMA\\scanningroom_tiles.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			albedomap.generateAnisotropicMipMaps(100);
			Texture specularmap = TextureManager.newTexture("res\\textures\\SOMA\\scanningroom_tiles_spec.png", PixelComponents.RGB, PixelFormat.R8);
			albedomap.generateAnisotropicMipMaps(100);
			Texture normalmap = TextureManager.newTexture("res\\textures\\SOMA\\scanningroom_tiles_nrm.bmp", PixelComponents.RGB, PixelFormat.RGB8);
			albedomap.generateAnisotropicMipMaps(100);

			DeferredMaterial material = renderer.getNewMaterial();
			material.setAlbedoTexture(albedomap);
			material.setSpecularMap(specularmap);
			material.setNormalMap(normalmap);
			material.setTextureRepeat(10,10);
			material.setSpecularity(40f);
			material.setRoughness(0.975f);
			Model3D model1 = ModelFactory.getModel("res/models/plane.obj", material);
			scene.add(model1);

			material = renderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res\\models\\SOMA\\ark\\albedo.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoTexture(albedo);
			Texture normals = TextureManager.newTexture("res\\models\\SOMA\\ark\\normals.bmp", PixelComponents.RGB, PixelFormat.RGB8);
			material.setNormalMap(normals);
			Texture specular = TextureManager.newTexture("res\\models\\SOMA\\ark\\specular.png", PixelComponents.RGB, PixelFormat.R8);
			material.setSpecularMap(specular);
			material.setRoughness(0.2f);
			material.setReflectivity(0.1f);
			Model3D model = ModelFactory.getModel("res\\models\\SOMA\\ark\\model.obj", material);
			model.setScale(10,10,10);
			scene.add(model);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(100);
		camera.setPosition(-1,7,19);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			//sincos += step * timescaler;

			Renderer.setRenderer(renderer);
			Renderer.render();
			Renderer.swapBuffers();
			renderer.renderAttachments(4);
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

