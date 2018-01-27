package engine.graphics.shading.materials;

import engine.graphics.shading.ShaderProgram;
import engine.graphics.shading.posteffects.PostEffectShader;

import java.io.IOException;

public class ShaderToyShader extends ShaderProgram {
	public ShaderToyShader(String vertexshaderpath, String fragmentshaderpath) throws IOException {
		super(vertexshaderpath, fragmentshaderpath);
	}

	protected void bindAttributes() { }

	@Override
	protected void getCustomUniformLocations() { getOptionalUniformLocations(); }
	@Override
	protected void setDefaultCustomUniformValues() { setOptionalDefaultUnifomValues(); }

	@Override
	public boolean supportsTransparency() {
		return false;
	}

}
