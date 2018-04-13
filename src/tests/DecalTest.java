package tests;

import engine.graphics.cameras.DebugCamera;
import engine.graphics.models.Decal;
import engine.graphics.models.Model3D;
import engine.graphics.rendering.*;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.lighting.PointLight;
import engine.graphics.textures.*;
import engine.math.Quaternion;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import java.io.IOException;

public class DecalTest {
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
		try {
			deferredrenderer = Renderer.getDeferedRenderer();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scene scene = deferredrenderer.getScene();

		PointLight light1 = new PointLight();
		light1.quadraticAttenuation = 0.003f;
		light1.setPosition(0,10,-10);
		scene.add(light1);

		scene.getAmbientlight().setColor(0.02f, 0.02f, .02f);

		try {
			Texture albedo = TextureManager.newTexture("res/textures/default.png", PixelComponents.RGB, PixelFormat.SRGB8);
			DeferredMaterial roommaterial = deferredrenderer.getNewMaterial();
			roommaterial.setAlbedoTexture(albedo);
			roommaterial.setTextureRepeat(4,4);
			Model3D room = new Model3D("res/models/decalsroom.obj", roommaterial);
			scene.add(room);

			DeferredMaterial bunnymaerial = deferredrenderer.getNewMaterial();
			Model3D bunny = new Model3D("res/models/bunny.obj", bunnymaerial);
			bunnymaerial.setAlbedoColor(1,1,1,1);
			bunny.setPosition(-6.794f, 0f, -8.714f);
			bunny.setScale(5,5,5);
			scene.add(bunny);

			albedo = TextureManager.newTexture("res/textures/decals/blood1.png", PixelComponents.RGBA, PixelFormat.SRGBA8);
			albedo.setFilteringMode(TextureFilter.Linear);
			Texture specular = TextureManager.newTexture("res/textures/Moss_Sticks02_disp.png", PixelComponents.RG, PixelFormat.RGB8);
			specular.setFilteringMode(TextureFilter.Linear);

			Decal decal = new Decal(albedo, specular);
			decal.setPosition(6.486f,-0.133f,8.030f);
			decal.setScale(10.380f, 0.395f, 7.796f);
			scene.add(decal);

			decal = new Decal(albedo, specular);
			decal.setPosition(-5.667f,-0.082f,-1.324f);
			decal.setScale(7.298f, 0.278f, 5.481f);
			scene.add(decal);

			decal = new Decal(albedo, specular);
			decal.setPosition(-7.631f,2.307f,-8.192f);
			decal.setScale(4.705f, 5.537f, 5.481f);
			scene.add(decal);

			Quaternion rotation = new Quaternion();
			decal = new Decal(albedo, specular);
			decal.setPosition(-4.991f,5.283f,-12.1f);
			decal.setScale(7.298f, 0.278f, 5.481f);
			rotation.rotate(90,0,0);
			decal.setRotation(rotation);
			scene.add(decal);

			decal = new Decal(albedo, specular);
			decal.setPosition(1.334f,2.082f,-11.032f);
			rotation.toIdentity();
			rotation.rotate(52f,0,0);
			decal.setRotation(rotation);
			decal.setScale(4.705f, 4.458f, 8.702f);
			scene.add(decal);

			decal = new Decal(albedo, specular);
			decal.setPosition(8.557f,-0.561f,-1.555f);
			rotation.toIdentity();
			rotation.rotate(13.578f,0,0);
			decal.setRotation(rotation);
			decal.setScale(4.705f, 3.566f, 8.702f);
			scene.add(decal);

			decal = new Decal(albedo, specular);
			decal.setPosition(10.929f,1.676f,-10.642f);
			rotation.toIdentity();
			rotation.rotate(28.287f,0,0);
			decal.setRotation(rotation);
			decal.setScale(5.000f, 6.237f, 5.000f);
			scene.add(decal);
		} catch (IOException | ShaderCompilationException e) {
			System.err.println("Couldn't load Model!");
			e.printStackTrace(System.err);
			exitCleanly(1);
		}

		DebugCamera camera = new DebugCamera();
		camera.setPosition(-1,7,19);
		scene.currentCamera = camera;

		System.gc();

		boolean isrunning = true;
		double rotation = 0, step = 0.1f;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			rotation += step * timescaler;

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.swapBuffers();
			deferredrenderer.renderAttachments();


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
