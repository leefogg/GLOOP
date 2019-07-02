package GLOOP.graphics.rendering;

import GLOOP.graphics.Settings;
import GLOOP.graphics.rendering.shading.*;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1i;
import GLOOP.graphics.rendering.shading.GLSL.Uniform3f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

		public void update(GLOOP.graphics.rendering.shading.lights.PointLight light) {
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

		public void update(GLOOP.graphics.rendering.shading.lights.DirectionalLight directionallight) {
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

		public void update(GLOOP.graphics.rendering.shading.lights.SpotLight spotlight) {
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

	private PointLight[] pointLights;
	private DirectionalLight[] directionalLights;
	private SpotLight[] spotLights;

	public GBufferDeferredLightingPassShader(Iterable<Map.Entry<String, String>> defines) throws ShaderCompilationException, IOException {
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
		directionalLightCount = new Uniform1i(this, "numberOfDirectionalLights");
		directionalLights = new DirectionalLight[Settings.MaxDirectionalLights];
		for (int i=0; i<directionalLights.length; i++)
			directionalLights[i] = new DirectionalLight(i, this);

		// Point Lights
		pointLightCount = new Uniform1i(this, "numberOfPointLights");
		pointLights = new PointLight[Settings.MaxPointLights];
		for (int i=0; i<pointLights.length; i++)
			pointLights[i] = new PointLight(i, this);
		//TODO: If more than 64 lights, find the 64 most contributing lights (closest)

		// Spot lights
		spotLightCount = new Uniform1i(this, "numberOfSpotLights");
		spotLights = new SpotLight[Settings.MaxSpotLights];
		for (int i=0; i<spotLights.length; i++)
			spotLights[i] = new SpotLight(i, this);

		time = new Uniform1f(this, "time");

		VolumetricLightsStrength = new Uniform1f(this, "VolumetricLightStrength");
	}

	public final void updateLights(
			List<GLOOP.graphics.rendering.shading.lights.PointLight> pointlights,
			List<GLOOP.graphics.rendering.shading.lights.SpotLight> spotlights,
			List<GLOOP.graphics.rendering.shading.lights.DirectionalLight> directionallights)
	{   //TODO: Upload all lights to a UBO
		// Directional lights
		// Point lights
		directionalLightCount.set(directionallights.size());
		for (int i=0; i<Math.min(directionallights.size(), directionalLights.length); i++)
			directionalLights[i].update(directionallights.get(i));

		// Point lights
		pointLightCount.set(pointlights.size());
		for (int i=0; i<Math.min(pointlights.size(), pointLights.length); i++)
			pointLights[i].update(pointlights.get(i));

		// Spot lights
		spotLightCount.set(spotlights.size());
		for (int i=0; i<Math.min(spotlights.size(), spotLights.length); i++)
			spotLights[i].update(spotlights.get(i));
	}

	public void setTime(float timeinseconds) { time.set(timeinseconds); }

	public void setVolumetricLightsStrength(float volumetricLightsStrength) { VolumetricLightsStrength.set(volumetricLightsStrength); }
}
