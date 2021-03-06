package gloop.graphics.rendering;

import gloop.graphics.Settings;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.texturing.CubeMap;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureManager;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public final class DeferredMaterial extends Material<DeferredGBuffersShader> {
	private static final Vector3f TEMP = new Vector3f();

	private Texture
		albedoMap,
		normalMap,
		specularMap,
		depthMap;
	private CubeMap environmentMap;
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
		roughness = 0.5f,
		fresnelBias = 0,
		fresnelScale = 1f,
		fresnelExponent = 10f;

	@Override
	public DeferredGBuffersShader getShader() {
		return DeferredRenderer.getGBuffersShader();
	}

	public void setAlbedoMap(Texture texture) {
		this.albedoMap = texture;
		setAlbedoColor(1, 1, 1, 1);
	}
	public Texture getAlbedoMap() {
		return albedoMap;
	}
	public void setNormalMap(Texture texture) {
		this.normalMap = texture;
	}
	public Texture getNormalMap() {
		return normalMap;
	}
	public void setSpecularMap(Texture texture) {
		this.specularMap = texture;
		specularity = 1;
	}
	public Texture getSpecularMap() {
		return specularMap;
	}
	public void setDepthMap(Texture texture) {
		this.depthMap = texture;
	}
	public Texture getDepthMap() { return depthMap; }
	public void setEnvironmentMap(CubeMap texture) { this.environmentMap = texture; }
	public CubeMap getEnvironmentMap() {
		return environmentMap;
	}

	public void setSpecularity(float specularity) { this.specularity = Math.min(Math.max(specularity / 100f,0),1); }

	public void setRoughness(float roughness) { this.roughness = Math.min(Math.max(roughness,0),1);}

	public final void setAlbedoColor(Vector4f diffusecolor) { setAlbedoColor(diffusecolor.x, diffusecolor.y, diffusecolor.z, diffusecolor.w); }
	public final void setAlbedoColor(float r, float g, float b, float a) {
		diffuseColor.set(r, g, b, a);
	}

	public final void setReflectivity(float reflectivity) { this.reflectivity = reflectivity; }

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
	private void setRefractionIndices(float r, float g, float b, float orginal) {	refractionIndices.set(r,g,b,orginal); }

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
			} else {
				shader.hasSpecularMap(true);
				TextureManager.bindSpecularMap(specularMap);
			}
		}
		shader.setSpecularity(specularity);
		shader.setRoughness(roughness);

		if (Settings.EnableFresnel) {
			shader.setFresnelBias(fresnelBias);
			shader.setFresnelScale(fresnelScale);
			shader.setFresnelExponent(fresnelExponent);
		}


		if (Settings.EnableEnvironemntMapping) {
			if (environmentMap == null) {
				shader.hasEnvironmentMap(false);
			} else {
				shader.hasEnvironmentMap(true);
				shader.setReflectivity(reflectivity);
				shader.setRefractivity(refractivity);
				shader.setRefractionIndices(refractionIndices);
				environmentMap.getPosition(TEMP);
				shader.setEnvironmentMapPosition(TEMP);
				environmentMap.getSize(TEMP);
				shader.setEnvironmentMapSize(TEMP);
				TextureManager.bindReflectionMap(environmentMap);
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

		shader.setTime(Viewport.getElapsedSeconds());
	}

	@Override
	public boolean usesDeferredPipeline() { return true; }

	@Override
	protected boolean hasTransparency() { return albedoMap != null && albedoMap.isTransparent(); }

	@Override
	public boolean supportsShadowMaps() { return true; }

	@Override
	public Texture getAlbedoTexture() { return albedoMap;	}
}
