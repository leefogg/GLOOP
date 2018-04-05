package engine.graphics.rendering;

import engine.graphics.cameras.Camera;
import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.*;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.textures.TextureUnit;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;

final class DeferredGBuffersShader extends ShaderProgram {
	private Uniform1i
		albedoMap,
		normalMap,
		specularMap,
		environmentMap,
		depthMap;
	private Uniform1f
		znear,
		zfar;
	private Uniform3f campos;
	private Uniform4f diffuseColor;
	private Uniform1f
			reflectivity,
			refractivity,
			normalMapScale,
			displacement,
			specularity,
			roughness,
			fresnelBias,
			fresnelScale,
			fresnelExponent,
			time;
	private Uniform2f textureRepeat;
	private Uniform2f textureOffset;
	private Uniform1b
		hasDiffuseMap,
		hasSpecularMap,
		hasNormalMap,
		hasEnvironmentMap,
		hasDepthMap;
	private Uniform4f refractionIndices;

	private static final Vector3f cameraposition = new Vector3f(); // Pass through

	public DeferredGBuffersShader(String[] defines) throws ShaderCompilationException, IOException {
		super(
			"res/shaders/deferredlightingshader/VertexShader.vert",
			"res/shaders/deferredlightingshader/FragmentShader.frag",
			defines
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
		bindAttribute("TextureCoords", VertexArray.TextureCoordinatesIndex);
		bindAttribute("VertexNormal", VertexArray.VertexNormalsIndex);
		bindAttribute("Tangent", VertexArray.VertexTangentsIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		znear 	= new Uniform1f(this, "znear");
		zfar 	= new Uniform1f(this, "zfar");
		campos 	= new Uniform3f(this, "campos");

		albedoMap 	= new Uniform1i(this, "albedoMap");
		hasDiffuseMap = new Uniform1b(this, "HasAlbedoMap");
		normalMap 	= new Uniform1i(this, "normalMap");
		hasNormalMap = new Uniform1b(this, "HasNormalMap");
		specularMap = new Uniform1i(this, "specularMap");
		hasSpecularMap = new Uniform1b(this, "HasSpecularMap");
		environmentMap = new Uniform1i(this, "environmentMap");
		hasEnvironmentMap = new Uniform1b(this, "HasEnvironmentMap");
		depthMap = new Uniform1i(this, "depthMap");
		hasDepthMap = new Uniform1b(this, "HasDepthMap");
		displacement = new Uniform1f(this, "heightScale");

		diffuseColor = new Uniform4f(this, "AlbedoColor");

		reflectivity = new Uniform1f(this, "reflectivity");
		refractivity = new Uniform1f(this, "refractivity");
		refractionIndices = new Uniform4f(this, "refractionIndices");
		normalMapScale = new Uniform1f(this, "normalMapScale");

		textureRepeat = new Uniform2f(this, "TextureRepeat");
		textureOffset = new Uniform2f(this, "TextureOffset");

		specularity = new Uniform1f(this, "Specularity");
		roughness = new Uniform1f(this, "Roughness");

		fresnelBias = new Uniform1f(this, "FresnelBias");
		fresnelScale = new Uniform1f(this, "FresnelScale");
		fresnelExponent = new Uniform1f(this, "FresnelPower");

		time = new Uniform1f(this, "Time");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setzfar(Camera.DEFAULT_ZFAR);
		setznear(Camera.DEFAULT_ZNEAR);
	}

	@Override
	public boolean supportsTransparency() {
		return true;
	}


	private void setAlbedoMap(int unit) { albedoMap.set(unit); }
	private void setNormalMap(int unit) {
		normalMap.set(unit);
	}
	private void setSpecularMap(int unit) {
		specularMap.set(unit);
	}
	private void setDepthMap(int unit) { depthMap.set(unit); }
	private void setEnvironmentMap(int unit) { environmentMap.set(unit); }

	public void hasDiffuseMap(boolean hasdiffusemap) {
		hasDiffuseMap.set(hasdiffusemap);
	}
	public void hasSpecularMap(boolean hasspecularmap) {
		hasSpecularMap.set(hasspecularmap);
	}
	public void hasNormalMap(boolean hasnormalmap) {
		hasNormalMap.set(hasnormalmap);
	}
	public void hasDepthMap(boolean hasdepthmap) { hasDepthMap.set(hasdepthmap); }
	public void hasEnvironmentMap(boolean hasenvironmentmap) { hasEnvironmentMap.set(hasenvironmentmap); }

	public void setSpecularity(float specularity) { this.specularity.set(specularity); }

	public void setDiffuseColor(Vector4f diffuse) {
		diffuseColor.set(diffuse);
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity.set(reflectivity);
	}

	public void setRefractivity(float refractivity) {
		this.refractivity.set(refractivity);
	}

	public void setRoughness(float roughness) { this.roughness.set(roughness); }

	public void setRefractionIndices(Vector4f indices) { refractionIndices.set(indices); }
	public void setRefractionIndices(float r, float g, float b, float original) { refractionIndices.set(r,g,b,original); }

	public void setNormalMapScale(float normalmapscale) { normalMapScale.set(normalmapscale); }

	public void setTextureRepeat(Vector2f texturerepeat) { textureRepeat.set(texturerepeat); }

	public void setTextureOffset(Vector2f textureoffset) { textureOffset.set(textureoffset); }

	public void setDisplacement(float displacement) { this.displacement.set(displacement); }

	public void setFresnelBias(float bias) { this.fresnelBias.set(bias); }

	public void setFresnelScale(float scale) { this.fresnelScale.set(scale); }

	public void setFresnelExponent(float exponent) { this.fresnelExponent.set(exponent); }

	public void setznear(float znear) {
		this.znear.set(znear);
	}

	public void setzfar(float zfar) {
		this.zfar.set(zfar);
	}

	public void setTime(float time) { this.time.set(time); }


	public void setCameraPosition(Vector3f cameraposition) {
		campos.set(cameraposition);
	}

	@Override
	public void setCameraUniforms(Camera camera, Matrix4f modelmatrix) {
		super.setCameraUniforms(camera, modelmatrix);

		setzfar(camera.getzfar());
		setznear(camera.getznear());
		camera.getPosition(cameraposition);
		setCameraPosition(cameraposition);

		//TODO: Why do I have to update these each frame?
		setAlbedoMap(TextureUnit.AlbedoMap);
		setNormalMap(TextureUnit.NormalMap);
		setSpecularMap(TextureUnit.SpecularMap);
		setDepthMap(TextureUnit.DepthMap);
		setEnvironmentMap(TextureUnit.EnvironmentMap);
	}
}
