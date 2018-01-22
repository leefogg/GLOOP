package engine.graphics.rendering;

import engine.graphics.cameras.Camera;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Scene;
import engine.graphics.shading.*;
import engine.graphics.shading.GLSL.Uniform16f;
import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.GLSL.Uniform1i;
import engine.graphics.shading.GLSL.Uniform3f;
import engine.graphics.shading.posteffects.PostEffectShader;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

final class LightingPassShader extends PostEffectShader {
	private Uniform1i
		positionTexture,
		normalTexture,
		specularTexture,

		pointLightCount,
		directionalLightCount,
		spotLightCount;

	private Uniform1f
		znear,
		zfar,
		time;
	private Uniform16f ViewMatrix;

	private Uniform3f
		ambientColor,
		campos;

	private static final Vector3f passthrough = new Vector3f();

	private class PointLight {
		private int index;

		private Uniform1f
				linearAttenuation,
				quadraticAttenuation;
		private Uniform3f
				position,
				color;

		public PointLight(int index, ShaderProgram shader) {
			this.index = index;

			position = new Uniform3f(shader, "pointLights[" + index + "].position");
			color = new Uniform3f(shader, "pointLights[" + index + "].color");
			linearAttenuation = new Uniform1f(shader, "pointLights[" + index + "].linearAttenuation");
			quadraticAttenuation =   new Uniform1f(shader, "pointLights[" + index + "].quadraticAttenuation");
		}

		public void update(Scene scene) {
			engine.graphics.shading.lighting.PointLight light = scene.getPointLight(index);
			light.getPosition(passthrough);
			position.set(passthrough);
			light.getColor(passthrough);
			color.set(passthrough);
			linearAttenuation.set(light.linearAttenuation);
			quadraticAttenuation.set(light.quadraticAttenuation);
		}
	}
	private class DirectionalLight {
		private int index;

		private Uniform3f
				direction,
				diffuse;

		public DirectionalLight(int index, ShaderProgram shader) {
			this.index = index;

			direction = new Uniform3f(shader, "directionalLights[" + index + "].direction");
			diffuse = new Uniform3f(shader, "directionalLights[" + index + "].diffuseColor");
		}

		public void update(Scene scene) {
			engine.graphics.shading.lighting.DirectionalLight directionallight = scene.getDirectionallight(index);
			directionallight.getDirection(passthrough);
			direction.set(passthrough);
			directionallight.getDiffuseColor(passthrough);
			diffuse.set(passthrough);
		}
	}
	private class SpotLight {
		private int index;

		private Uniform3f
				position,
				direction,
				color;
		private Uniform1f innerCone,
				outerCone,
				linearAttenuation,
				quadraticAttenuation;

		public SpotLight(int index, ShaderProgram shader) {
			this.index = index;

			position = new Uniform3f(shader, "spotLights[" + index + "].position");
			direction = new Uniform3f(shader, "spotLights[" + index + "].direction");
			color = new Uniform3f(shader, "spotLights[" + index + "].color");
			innerCone = new Uniform1f(shader, "spotLights[" + index + "].innerCone");
			outerCone = new Uniform1f(shader, "spotLights[" + index + "].outerCone");
			linearAttenuation = new Uniform1f(shader, "spotLights[" + index + "].linearAttenuation");
			quadraticAttenuation = new Uniform1f(shader, "spotLights[" + index + "].quadraticAttenuation");
		}

		public void update(Scene scene) {
			engine.graphics.shading.lighting.SpotLight spotlight = scene.getSpotLight(index);
			spotlight.getPosition(passthrough);
			position.set(passthrough);
			spotlight.getDirection(passthrough);
			direction.set(passthrough);
			spotlight.getColor(passthrough);
			color.set(passthrough);
			innerCone.set(spotlight.getInnerCone());
			outerCone.set(spotlight.getOuterCone());
			linearAttenuation.set(spotlight.linearAttenuation);
			quadraticAttenuation.set(spotlight.quadraticAttenuation);
		}
	}


	private PointLight[] pointLights;
	private DirectionalLight[] directionalLights;
	private SpotLight[] spotLights;

	private static final Vector3f cameraposition = new Vector3f(); // Pass through

	public LightingPassShader(String[] defines) throws ShaderCompilationException, IOException {
		super(
			"res/shaders/Post Effects/DeferredShading/LightPass/VertexShader.vert",
			"res/shaders/Post Effects/DeferredShading/LightPass/FragmentShader.frag",
			defines
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		// Buffers
		positionTexture = new Uniform1i(this, "positionTexture");
		normalTexture 	= new Uniform1i(this, "normalTexture");
		specularTexture = new Uniform1i(this, "specularTexture");

		// Camera
		znear 	= new Uniform1f(this, "znear");
		zfar 	= new Uniform1f(this, "zfar");
		campos 	= new Uniform3f(this, "campos");
		ViewMatrix = new Uniform16f(this, "ViewMatrix");

		// Lights
		// Ambience
		ambientColor = new Uniform3f(this, "ambientLight");

		directionalLightCount = new Uniform1i(this, "numberOfDirectionalLights");
		directionalLights = new DirectionalLight[8];
		for (int i=0; i<directionalLights.length; i++)
			directionalLights[i] = new DirectionalLight(i, this);

		// Point Lights
		pointLightCount = new Uniform1i(this, "numberOfPointLights");
		pointLights = new PointLight[64];
		for (int i=0; i<pointLights.length; i++)
			pointLights[i] = new PointLight(i, this);
		//TODO: If more than 64 lights, find the 64 most contributing lights (closest)

		// Spot lights
		spotLightCount = new Uniform1i(this, "numberOfSpotLights");
		spotLights = new SpotLight[32];
		for (int i=0; i<spotLights.length; i++)
			spotLights[i] = new SpotLight(i, this);

		time = new Uniform1f(this, "time");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		bindGBuffers();
	}

	public void bindGBuffers() {
		setNormalTexture(TextureUnit.GBuffer_Normal);
		setPositionTexture(TextureUnit.GBuffer_Position);
		setSpecularTexture(TextureUnit.GBuffer_Specular);
	}

	public final void updateLights() { //TODO: Upload all lights to a UBO
		Scene scene = Renderer.getRenderer().getScene();

		// Ambient light
		scene.getAmbientlight().getColor(passthrough);
		ambientColor.set(passthrough);

		// Directional lights
		// Point lights
		directionalLightCount.set(scene.getNumberOfDirectionalLights());
		for (int i=0; i<Math.min(scene.getNumberOfDirectionalLights(), directionalLights.length); i++) {
			DirectionalLight directionallight = directionalLights[i];
			directionallight.update(scene);
		}

		// Point lights
		pointLightCount.set(scene.getNumberOfPointLights());
		for (int i=0; i<Math.min(scene.getNumberOfPointLights(), pointLights.length); i++) {
			PointLight spotlight = pointLights[i];
			spotlight.update(scene);
		}

		// Spot lights
		spotLightCount.set(scene.getNumberOfSpotLights());
		for (int i=0; i<Math.min(scene.getNumberOfSpotLights(), spotLights.length); i++) {
			SpotLight spotlight = spotLights[i];
			spotlight.update(scene);
		}
	}


	public final void setPositionTexture(int unit) { positionTexture.set(unit);	}

	public final void setNormalTexture(int unit) { normalTexture.set(unit); }

	public final void setSpecularTexture(int unit) { specularTexture.set(unit); }

	public void setznear(float znear) { this.znear.set(znear); }

	public void setzfar(float zfar) { this.zfar.set(zfar); }

	public void setCameraPosition(Vector3f cameraposition) { campos.set(cameraposition); }

	public void setTime(float timeinseconds) { time.set(timeinseconds); }


	public void setCameraAttributes(Camera camera) {
		setzfar(camera.getzfar());
		setznear(camera.getznear());
		camera.getPosition(cameraposition);
		setCameraPosition(cameraposition);
		ViewMatrix.set(camera.getViewMatrix());
	}
}
