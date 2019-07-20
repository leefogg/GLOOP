package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import GLOOP.graphics.rendering.texturing.Texture;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Material<T extends ShaderProgram> {
	public static DepthMaterial DepthWithTexture, DepthWithoutTexture;

	public static void CreateShadowMapShader() throws IOException {
		Map<String, String> defines = new HashMap<>();
		DepthWithoutTexture = new DepthMaterial(new DepthShader(defines.entrySet()));
		defines.put("HasTexture", "");
		DepthWithTexture = new DepthMaterial(new DepthShader(defines.entrySet()));
	}

	public void bind() {
		getShader().bind();
	}

	public abstract T getShader();

	public abstract void commit();

	public void setCameraAttributes(Camera currentcamera, Matrix4f modelmatrix) {
		getShader().setCameraUniforms(currentcamera, modelmatrix);
	}

	public boolean isTransparent() { return getShader().supportsTransparency() && hasTransparency(); }

	public boolean usesDeferredPipeline() { return false; } // TODO: Maybe use a private boolean?

	protected abstract boolean hasTransparency();
	public abstract boolean SupportsShadowMaps();
	public Texture GetAlbedoTexture() { return null; }
	public DepthMaterial ToShadowMapMaterial() {
		Texture albedo = GetAlbedoTexture();
		if (albedo != null && albedo.isTransparent()) {
			DepthWithTexture.setAlbedoMap(albedo);
			return DepthWithTexture;
		}

		return DepthWithoutTexture;
	}
}
