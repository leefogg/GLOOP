package GLOOP.graphics.rendering;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.rendering.shading.*;
import GLOOP.graphics.rendering.shading.GLSL.Uniform16f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import GLOOP.graphics.rendering.shading.posteffects.PostEffectShader;
import GLOOP.graphics.rendering.texturing.TextureUnit;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;

final class GBufferDeferredLightingPassShader extends GBufferLightingShader {
	private Uniform1i
		pointLightCount,
		directionalLightCount,
		spotLightCount;

	private Uniform1f
		time,
		VolumetricLightsStrength;

	private Uniform3f ambientColor;

	private static final Vector3f passthrough = new Vector3f();

	private class PointLight {
		private int index;

		private Uniform1f quadraticAttenuation;
		private Uniform3f
				position,
				color;

		public PointLight(int index, ShaderProgram shader) {
			this.index = index;

			position = new Uniform3f(shader, "pointLights[" + index + "].position");
			color = new Uniform3f(shader, "pointLights[" + index + "].color");
			quadraticAttenuation =   new Uniform1f(shader, "pointLights[" + index + "].quadraticAttenuation");
		}

		public void update(Scene scene) {
			GLOOP.graphics.rendering.shading.lights.PointLight light = scene.getPointLight(index);
			light.getPosition(passthrough);
			position.set(passthrough);
			light.getColor(passthrough);
			color.set(passthrough);
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
			GLOOP.graphics.rendering.shading.lights.DirectionalLight directionallight = scene.getDirectionallight(index);
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
				quadraticAttenuation;

		public SpotLight(int index, ShaderProgram shader) {
			this.index = index;

			position = new Uniform3f(shader, "spotLights[" + index + "].position");
			direction = new Uniform3f(shader, "spotLights[" + index + "].direction");
			color = new Uniform3f(shader, "spotLights[" + index + "].color");
			innerCone = new Uniform1f(shader, "spotLights[" + index + "].innerCone");
			outerCone = new Uniform1f(shader, "spotLights[" + index + "].outerCone");
			quadraticAttenuation = new Uniform1f(shader, "spotLights[" + index + "].quadraticAttenuation");
		}

		public void update(Scene scene) {
			GLOOP.graphics.rendering.shading.lights.SpotLight spotlight = scene.getSpotLight(index);
			spotlight.getPosition(passthrough);
			position.set(passthrough);
			spotlight.getDirection(passthrough);
			direction.set(passthrough);
			spotlight.getColor(passthrough);
			color.set(passthrough);
			innerCone.set(spotlight.getInnerCone());
			outerCone.set(spotlight.getOuterCone());
			quadraticAttenuation.set(spotlight.getQuadraticAttenuation());
		}
	}
	private class Fog {
		public final Uniform3f color;
		public final Uniform1f density;

		public Fog(ShaderProgram shader) {
			color = new Uniform3f(shader, "fogColor");
			density = new Uniform1f(shader, "fogDensity");
		}

		public void setColor(Vector3f color) { this.color.set(color); }
		public void setDensity(float density) { this.density.set(density); }
	}

	private PointLight[] pointLights;
	private DirectionalLight[] directionalLights;
	private SpotLight[] spotLights;
	private Fog fog;

	private static final Vector3f cameraposition = new Vector3f(); // Pass through

	public GBufferDeferredLightingPassShader(String[] defines) throws ShaderCompilationException, IOException {
		super(
			"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/VertexShader.vert",
			"res/_SYSTEM/Shaders/PostEffects/DeferredShading/LightPass/FragmentShader.frag",
			defines
		);
	}

	@Override
	protected void getCustomUniformLocations() {
		super.getCustomUniformLocations();

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

		fog = new Fog(this);

		time = new Uniform1f(this, "time");

		VolumetricLightsStrength = new Uniform1f(this, "VolumetricLightStrength");
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

		scene.getFogColor(passthrough);
		fog.setColor(passthrough);
		fog.setDensity(scene.getFogDensity());
	}

	public void setTime(float timeinseconds) { time.set(timeinseconds); }

	public void setVolumetricLightsStrength(float volumetricLightsStrength) { VolumetricLightsStrength.set(volumetricLightsStrength); }
}
