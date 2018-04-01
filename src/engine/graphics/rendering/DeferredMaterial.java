package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.shading.materials.Material;
import engine.graphics.textures.CubeMap;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureManager;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public final class DeferredMaterial extends Material<DeferredGBuffersShader> {
	private Texture
		albedoMap,
		normalMap,
		specularMap,
		depthMap;
	private CubeMap reflectionMap;
	private final Vector4f
			diffuseColor = new Vector4f(1, 0, 1, 1),
			refractionIndices = new Vector4f(1f/1.2f + 0.005f, 1f/1.2f + 0.010f, 1f/1.2f + 0.015f, 1f/1.2f);
	private final Vector2f textureRepeat = new Vector2f(1, 1);
	private final Vector2f textureOffset = new Vector2f(0, 0);
	private float
		reflectivity = 0,
		refractivity = 0,
		normalMapScale = 1,
		displacement = 0.05f,
		specularity = 0,
		roughness = 0,
		fresnelBias = 0,
		fresnelScale = 0.5f,
		fresnelExponent = 2f;
	public static final float MaxSpecularExponent = 256f;

	@Override
	public DeferredGBuffersShader getShader() {
		return DeferredRenderer.getGBuffersShader();
	}

	public void setAlbedoTexture(Texture texture) {
		this.albedoMap = texture;
		setDiffuseColor(1, 1, 1, 1);
	}
	public Texture getAlbedoTexture() {
		return albedoMap;
	}
	public void setNormalTexture(Texture texture) {
		this.normalMap = texture;
	}
	public Texture getNormalTexture() {
		return normalMap;
	}
	public void setSpecularTexture(Texture texture) {
		this.specularMap = texture;
	}
	public Texture getSpecularTexture() {
		return specularMap;
	}
	public void setDepthMap(Texture texture) {
		this.depthMap = texture;
	}
	public Texture getDepthMap() { return depthMap; }
	public void setEnvironmentTexture(CubeMap texture) { this.reflectionMap = texture; }
	public CubeMap getEnvironmentTexture() {
		return reflectionMap;
	}

	public void setSpecularity(float specularity) { this.specularity = specularity; }

	public void setRoughness(float roughness) { this.roughness = roughness;}

	public final void setDiffuseColor(Vector4f diffusecolor) { setDiffuseColor(diffusecolor.x, diffusecolor.y, diffusecolor.z, diffusecolor.w); }
	public final void setDiffuseColor(float r, float g, float b, float a) {
		diffuseColor.set(r, g, b, a);
	}

	public final void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public final void setRefractivity(float refractivity) {
		this.refractivity = refractivity;
	}

	public final void setNormalMapScale(float normalmapscaler) { this.normalMapScale = normalmapscaler; }

	public final void setTextureRepeat(float x, float y) { textureRepeat.set(x, y); }

	public final void setTextureOffset(float x, float y) { textureOffset.set(x, y); }

	public final void setDisplacement(float displacement) { this.displacement = displacement; }

	public final void setRefractionIndex(float refractionindex) {
		refractionindex = 1f/refractionindex;
		refractionindex = Math.max(0, refractionindex);
		setRefractionIndices(refractionindex + 0.005f, refractionindex + 0.010f, refractionindex + 0.015f, refractionindex);
	}
	public final void setRefractionIndices(Vector3f indices) { setRefractionIndices(indices.x, indices.y, indices.z); }
	public final void setRefractionIndices(float r, float g, float b) {
		r = Math.max(r, 0);
		g = Math.max(g, 0);
		b = Math.max(b, 0);
		setRefractionIndices(r,g,b, refractionIndices.w);
	}
	private final void setRefractionIndices(float r, float g, float b, float orginal) {	refractionIndices.set(r,g,b,orginal); }

	public final void setFresnelBias(float bias) { fresnelBias = bias; }

	public final void setFresnelScale(float scale) { fresnelScale = scale; }

	public final void setFresnelExponent(float exponent) { fresnelExponent = exponent; }

	@Override
	public void commit() {
		DeferredGBuffersShader shader = getShader();
		//TODO: Why do I have to update these each frame?
		shader.setTextureRepeat(textureRepeat);
		shader.setTextureOffset(textureOffset);

		if (albedoMap == null) {
			shader.hasDiffuseMap(false);
			shader.setDiffuseColor(diffuseColor);
		} else {
			shader.hasDiffuseMap(true);
			TextureManager.bindAlbedoMap(albedoMap);
		}

		if (Settings.EnableNormalMapping) {
			if (normalMap == null) {
				shader.hasNormalMap(false);
			} else {
				shader.hasNormalMap(true);
				shader.setNormalMapScale(normalMapScale);
				TextureManager.bindNormalMap(normalMap);
			}
		}

		if (Settings.EnableSpecularMapping) {
			if (specularMap == null) {
				shader.hasSpecularMap(false);
				shader.setSpecularity(specularity);
			} else {
				shader.hasSpecularMap(true);
				TextureManager.bindSpecularMap(specularMap);
			}
			shader.setRoughness(roughness);
		}

		if (Settings.EnableFresnel) {
			shader.setFresnelBias(fresnelBias);
			shader.setFresnelScale(fresnelScale);
			shader.setFresnelExponent(fresnelExponent);
		}


		if (Settings.EnableEnvironemntMapping) {
			if (reflectionMap == null) {
				shader.hasEnvironmentMap(false);
			} else {
				shader.hasEnvironmentMap(true);
				shader.setReflectivity(reflectivity);
				shader.setRefractivity(refractivity);
				shader.setRefractionIndices(refractionIndices);
				TextureManager.bindReflectionMap(reflectionMap);
			}
		}

		if (Settings.EnableParallaxMapping) {
			if (depthMap == null) {
				shader.hasDepthMap(false);
			} else {
				shader.hasDepthMap(true);
				shader.setDisplacement(displacement);
				TextureManager.bindDepthMap(depthMap);
			}
		}

		shader.setCameraAttributes(Renderer.getRenderer().getScene().currentCamera);
	}

	@Override
	public boolean useDeferredPipeline() { return true; }

	@Override
	protected boolean hasTransparency() { return albedoMap.isTransparent(); }
}
