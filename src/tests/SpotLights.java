package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.lights.SpotLight;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.io.IOException;

public final class SpotLights {
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

		SpotLight light = new SpotLight();
		light.setPosition(0,25,0);
		light.setQuadraticAttenuation(0.01f);
		light.setInnerCone(20);
		light.setOuterCone(50);
		light.setColor(1,0,0);
		scene.getAmbientlight().setColor(0.05f,0.05f,0.05f);
		scene.add(light);


		try {
			DeferredMaterial boxmaterial = renderer.getNewMaterial();
			Texture albedo = TextureManager.newTexture("res\\textures\\default.png", PixelComponents.RGB, PixelFormat.SRGB8);
			boxmaterial.setAlbedoTexture(albedo);
			boxmaterial.setTextureRepeat(5,5);
			Model3D outerbox = ModelFactory.getModel("res\\models\\insideout box.obj", boxmaterial);
			outerbox.setScale(50,50,50);
			outerbox.setPosition(0,25,0);
			scene.add(outerbox);

			DeferredMaterial material = renderer.getNewMaterial();
			albedo = TextureManager.newTexture("res\\models\\SOMA\\ark\\albedo.bmp", PixelComponents.RGB, PixelFormat.SRGB8);
			material.setAlbedoTexture(albedo);
			Texture normals = TextureManager.newTexture("res\\models\\SOMA\\ark\\normals.bmp", PixelComponents.RGB, PixelFormat.RGB8);
			material.setNormalMap(normals);
			Texture specular = TextureManager.newTexture("res\\models\\SOMA\\ark\\specular.png", PixelComponents.RGB, PixelFormat.R8);
			material.setSpecularMap(specular);
			material.setRoughness(0.2f);
			material.setReflectivity(0.1f);
			Model3D model = ModelFactory.getModel("res\\models\\lagsphere.obj", material);
			model.setScale(10,10,10);
			scene.add(model);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			System.err.println(e.getMessage());
			exitCleanly(1);
		} catch (UnsupportedException e) {
			e.printStackTrace();
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		scene.setDebugCamera(camera);
		scene.setGameCamera(camera);
		camera.setzfar(100);
		camera.setPosition(-1,25,60);

		System.gc();

		boolean isrunning = true;
		double sincos = (float)Math.PI, step = (float)Math.PI/300f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			sincos += step * timescaler;
			light.setDirection((float)Math.cos(sincos), (float)Math.sin(sincos), 0);

			Renderer.setRenderer(renderer);
			Renderer.render();
			Renderer.swapBuffers();
			renderer.renderAttachments(6);
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

