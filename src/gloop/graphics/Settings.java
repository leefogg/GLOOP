package gloop.graphics;

public abstract class Settings {
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
			EnableFog = false,
			EnableHDR = false,
			EnableVolumetricLights = false;
	public static int
			MaxPointLights = 32,
			MaxSpotLights = 32,
			MaxDirectionalLights = 8;
}