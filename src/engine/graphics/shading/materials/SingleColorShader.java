package engine.graphics.shading.materials;

import engine.graphics.models.VertexArray;
import engine.graphics.shading.GLSL.Uniform4f;
import engine.graphics.shading.ShaderCompilationException;
import engine.graphics.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;

public final class SingleColorShader extends ShaderProgram {
	private Uniform4f Color;

	public SingleColorShader() throws ShaderCompilationException, IOException {
		super(
				"res/shaders/ColorShader/VertexShader.vert",
				"res/shaders/ColorShader/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VertciesIndex);
	}

	@Override
	protected void getCustomUniformLocations() {
		Color = new Uniform4f(this, "color");
	}

	@Override
	protected void setDefaultCustomUniformValues() {
		setColor(1,0,1,1);
	}

	@Override
	public boolean supportsTransparency() {
		return false;
	}

	public void setColor(Vector4f color) { setColor(color.x, color.y, color.z, color.z); }
	public void setColor(float r, float g, float b, float a) { Color.set(r,g,b,a); }
}
