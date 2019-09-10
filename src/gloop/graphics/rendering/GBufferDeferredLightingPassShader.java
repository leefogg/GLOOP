package gloop.graphics.rendering;

import gloop.graphics.Settings;
import gloop.graphics.rendering.shading.*;
import gloop.graphics.rendering.shading.glsl.Uniform1f;
import gloop.graphics.rendering.shading.glsl.Uniform1i;
import gloop.graphics.rendering.shading.glsl.Uniform3f;
import org.lwjgl.util.vector.Vector3f;

import java.io.IOException;
import java.util.List;
import java.util.Map;

final class GBufferDeferredLightingPassShader extends GBufferLightingShader {
	private static final Vector3f PASSTHROUGH = new Vector3f();

	private Uniform1i
		pointLightCount,
		directionalLightCount,
		spotLightCount;
	private Uniform1f
		time,
		volumetricLightsStrength;
	private Uniform3f ambientColor;


	private static class PointLight {
		private final int index;

		private final Uniform1f quadraticAttenuation;
		private final Uniform3f
				position;
		private final Uniform3f color;

		public PointLight(int index, ShaderProgram shader) {
			this.index = index;

			position = new Uniform3f(shader, "pointLights[" + index + "].position");
			color = new Uniform3f(shader, "pointLights[" + index + "].color");
			quadraticAttenuation =   new Uniform1f(shader, "pointLights[" + index + "].quadraticAttenuation");
		}

		public void update(gloop.graphics.rendering.shading.lights.PointLight light) {
			light.getPosition(PASSTHROUGH);
			position.set(PASSTHROUGH);
			light.getColor(PASSTHROUGH);
			color.set(PASSTHROUGH);
			quadraticAttenuation.set(light.quadraticAttenuation);
		}
	}
	private static class DirectionalLight {
		private final int index;

		private final Uniform3f
				direction;
		private final Uniform3f diffuse;

		public DirectionalLight(int index, ShaderProgram shader) {
			this.index = index;

			direction = new Uniform3f(shader, "directionalLights[" + index + "].direction");
			diffuse = new Uniform3f(shader, "directionalLights[" + index + "].diffuseColor");
		}

		public void update(gloop.graphics.rendering.shading.lights.DirectionalLight directionallight) {
			directionallight.getDirection(PASSTHROUGH);
			direction.set(PASSTHROUGH);
			directionallight.getDiffuseColor(PASSTHROUGH);
			diffuse.set(PASSTHROUGH);
		}
	}
	private static class SpotLight {
		private final int index;

		private final Uniform3f
				position;
		private final Uniform3f direction;
		private final Uniform3f color;
		private final Uniform1f innerCone;
		private final Uniform1f outerCone;
		private final Uniform1f quadraticAttenuation;

		public SpotLight(int index, ShaderProgram shader) {
			this.index = index;

			position = new Uniform3f(shader, "spotLights[" + index + "].position");
			direction = new Uniform3f(shader, "spotLights[" + index + "].direction");
			color = new Uniform3f(shader, "spotLights[" + index + "].color");
			innerCone = new Uniform1f(shader, "spotLights[" + index + "].innerCone");
			outerCone = new Uniform1f(shader, "spotLights[" + index + "].outerCone");
			quadraticAttenuation = new Uniform1f(shader, "spotLights[" + index + "].quadraticAttenuation");
		}

		public void update(gloop.graphics.rendering.shading.lights.SpotLight spotlight) {
			spotlight.getPosition(PASSTHROUGH);
			position.set(PASSTHROUGH);
			spotlight.getDirection(PASSTHROUGH);
			direction.set(PASSTHROUGH);
			spotlight.getColor(PASSTHROUGH);
			color.set(PASSTHROUGH);
			innerCone.set((float)Math.cos(Math.toRadians(spotlight.getInnerCone())));
			outerCone.set((float)Math.cos(Math.toRadians(spotlight.getOuterCone())));
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

		volumetricLightsStrength = new Uniform1f(this, "VolumetricLightStrength");
	}

	public final void updateLights(
			List<gloop.graphics.rendering.shading.lights.PointLight> pointlights,
			List<gloop.graphics.rendering.shading.lights.SpotLight> spotlights,
			List<gloop.graphics.rendering.shading.lights.DirectionalLight> directionallights)
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

	public void setVolumetricLightsStrength(float volumetricLightsStrength) { this.volumetricLightsStrength.set(volumetricLightsStrength); }
}
