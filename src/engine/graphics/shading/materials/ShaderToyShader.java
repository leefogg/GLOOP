package engine.graphics.shading.materials;

import engine.graphics.rendering.Viewport;
import engine.graphics.shading.GLSL.Uniform1f;
import engine.graphics.shading.GLSL.Uniform2f;
import engine.graphics.shading.ShaderProgram;
import engine.graphics.shading.posteffects.PostEffectShader;

import java.io.IOException;

public class ShaderToyShader extends ShaderProgram {
	private Uniform2f
			resolution,
			mouse;
	private Uniform1f time;

	public ShaderToyShader(String vertexshaderpath, String fragmentshaderpath) throws IOException {
		super(vertexshaderpath, fragmentshaderpath);
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
