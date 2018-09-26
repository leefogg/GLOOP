package GLOOP.graphics.rendering.shading.materials;

import GLOOP.graphics.data.models.VertexArray;
import GLOOP.graphics.rendering.shading.GLSL.Uniform4f;
import GLOOP.graphics.rendering.shading.ShaderCompilationException;
import GLOOP.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;

public final class SingleColorShader extends ShaderProgram {
	private Uniform4f Color;

	public SingleColorShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/SingleColor/VertexShader.vert",
				"res/_SYSTEM/Shaders/SingleColor/FragmentShader.frag"
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
