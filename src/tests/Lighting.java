package tests;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.Settings;
import GLOOP.graphics.cameras.DebugCamera;
import GLOOP.graphics.data.models.Model3D;
import GLOOP.graphics.data.models.ModelFactory;
import GLOOP.graphics.rendering.*;
import GLOOP.graphics.rendering.DeferredMaterial;
import GLOOP.graphics.rendering.DeferredRenderer;
import GLOOP.graphics.rendering.shading.lights.PointLight;
import GLOOP.graphics.rendering.shading.materials.SingleColorMaterial;
import GLOOP.graphics.rendering.shading.posteffects.BloomPostEffect;
import GLOOP.graphics.rendering.texturing.PixelComponents;
import GLOOP.graphics.rendering.texturing.PixelFormat;
import GLOOP.graphics.rendering.texturing.Texture;
import GLOOP.graphics.rendering.texturing.TextureManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.Random;

public final class Lighting {
	private static final Random r = new Random();

	private static class LightBall {
		Vector3f position = new Vector3f(r.nextFloat() * 50 - 25, r.nextFloat() * 50, r.nextFloat() * 50 - 25);
		Vector3f vecolcity = new Vector3f(r.nextFloat()-0.5f, r.nextFloat()-0.5f, r.nextFloat()-0.5f);
		Vector3f color = new Vector3f();
		private PointLight light = new PointLight();
		private Model3D model;

		public LightBall(Scene scene, Model3D model) {
			vecolcity.scale(0.3f);
			light.quadraticAttenuation = 0.32f;
			color.set(r.nextFloat(), r.nextFloat(), r.nextFloat());
			color.normalise();
			color.scale(2f);
			light.setColor(color);
			light.setPosition(position.x, position.y, position.z);

			((SingleColorMaterial)model.getMaterial()).setColor(color);
			this.model = model;

			scene.add(light);
			scene.add(model);
		}

		public void update(double timescaler) {
			collideWalls();

			position.x += vecolcity.x;
			position.y += vecolcity.y;
			position.z += vecolcity.z;

			light.setPosition(position);
			model.setPosition(position);
		}

		private void collideWalls() {
			if (position.x > 25 || position.x < -25)
				vecolcity.x = -vecolcity.x;
			if (position.y > 50 || position.y < 0)
				vecolcity.y = -vecolcity.y;
			if (position.z > 25 || position.z < -25)
				vecolcity.z = -vecolcity.z;
		}
	}

	public static void main(String[] args) {
		try {
			Viewport.create(1280, 720, "Engine Testing");
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
		} catch (Exception e) {
			e.printStackTrace();
			exitCleanly(1);
		}
		Scene scene = deferredrenderer.getScene();
		forwardrenderer.setScene(scene);

		LightBall[] balls = new LightBall[64];
		try {
			DeferredMaterial wallsmaterial = deferredrenderer.getNewMaterial();
			wallsmaterial.setAlbedoColor(1,1,1,1);
			//Texture albedomap = TextureManager.newTexture("res\\textures\\plane-d.png", PixelComponents.RGB, PixelFormat.SRGB8);
			//floormaterial.setAlbedoTexture(albedomap);
			wallsmaterial.setAlbedoColor(1,1,1,1);
			Texture normalmap = TextureManager.newTexture("res\\textures\\plane-n.png", PixelComponents.RGB, PixelFormat.RGB8);
			wallsmaterial.setNormalMap(normalmap);
			Texture specularmap = TextureManager.newTexture("res\\textures\\plane-s.png", PixelComponents.RGBA, PixelFormat.R8);
			wallsmaterial.setSpecularMap(specularmap);
			wallsmaterial.setTextureRepeat(2,2);
			wallsmaterial.setSpecularity(1);
			wallsmaterial.setRoughness(0.5f);
			Model3D outerbox = ModelFactory.getModel("res\\models\\insideout box.obj", wallsmaterial);
			outerbox.setScale(50,50,50);
			outerbox.setPosition(0,25,0);
			scene.add(outerbox);

			DeferredMaterial material = deferredrenderer.getNewMaterial();
			material.setAlbedoColor(1,1,1,1);
			Model3D box = ModelFactory.getModel("res\\models\\bunny.obj", material);
			box.setPosition(0, 25, 0);
			box.setScale(10,10,10);
			scene.add(box);

			for (int i=0; i<balls.length; i++)
				balls[i] = new LightBall(scene, ModelFactory.getModel("res\\models\\sphere.obj",  new SingleColorMaterial()));


			BloomPostEffect bloompost = new BloomPostEffect();
			bloompost.setNumberOfPasses(3);
			Renderer.addPostEffect(bloompost);
			Renderer.enablePostEffects();
		} catch (IOException e) {
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
		camera.setPosition(0, 25, 40);

		System.gc();

		boolean isrunning = true;
		while(isrunning) {
			Viewport.update();
			float delta = Renderer.getTimeDelta();
			float timescaler = Renderer.getTimeScaler();
			camera.update(delta, timescaler);

			for (LightBall ball : balls)
				ball.update(timescaler);

			Renderer.setRenderer(deferredrenderer);
			Renderer.render();
			Renderer.setRenderer(forwardrenderer);
			Renderer.render();
			Renderer.swapBuffers();
			deferredrenderer.renderAttachments();


			Viewport.setTitle("Development Engine " + Viewport.getCurrentFrameRate() + "Hz");

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