package engine.graphics;

public class Settings {
	// Deferred rendering settings
	public static boolean
			EnableDither = true,
			EnableSpecularMapping = true,
			EnableParallaxMapping = false,
			EnableNormalMapping = true,
			EnableEnvironemntMapping = true,
			EnableReflectivity = true,
			EnableRefractivity = true,
			EnableFresnel = false,
			EnableChromaticAberration = false,
			EnableShadows = false,
			EnableFog = false;
	public int
			MaxPointLights = 64,
			MaxSpotLights = 32,
			MaxDirectionalLights = 8;
}