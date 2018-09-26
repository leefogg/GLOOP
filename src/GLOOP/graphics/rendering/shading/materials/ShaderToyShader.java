package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.rendering.Viewport;
import GLOOP.graphics.rendering.shading.GLSL.Uniform1f;
import GLOOP.graphics.rendering.shading.GLSL.Uniform2f;
import GLOOP.graphics.rendering.shading.posteffects.PostEffectShader;

import java.io.IOException;

public class ShaderToyShader extends PostEffectShader {
	private Uniform2f
			resolution,
			mouse;
	private Uniform1f time;

	public ShaderToyShader(String fragmentshaderpath) throws IOException {
		super(
				"res/_SYSTEM/Shaders/PostEffects/shadertoy/vertexShader.vert",
				fragmentshaderpath
		);
	}

	protected void bindAttributes() { }

	@Override
	protected void getCustomUniformLocations() {
		resolution = new Uniform2f(this, "Resolution");
		time = new Uniform1f(this, "Time");
		mouse = new Uniform2f(this, "Mouse");
	}
	@Override
	protected void setDefaultCustomUniformValues() {
		resolution.set(Viewport.getWidth(), Viewport.getHeight());
		time.set(Viewport.getElapsedSeconds());
	}

	public void setResolution(float width, float height) { resolution.set(width, height); }

	public void setTime(float seconds) { time.set(seconds); }

	public void setMousePosition(float x, float y) { mouse.set(x,y); }

	@Override
	public boolean supportsTransparency() {
		return false;
	}

}
