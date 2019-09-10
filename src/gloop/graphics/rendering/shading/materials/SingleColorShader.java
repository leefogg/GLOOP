package gloop.graphics.rendering.shading.materials;

import gloop.graphics.data.models.VertexArray;
import gloop.graphics.rendering.shading.glsl.Uniform4f;
import gloop.graphics.rendering.shading.ShaderCompilationException;
import gloop.graphics.rendering.shading.ShaderProgram;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;

public final class SingleColorShader extends ShaderProgram {
	private Uniform4f color;

	public SingleColorShader() throws ShaderCompilationException, IOException {
		super(
				"res/_SYSTEM/Shaders/SingleColor/VertexShader.vert",
				"res/_SYSTEM/Shaders/SingleColor/FragmentShader.frag"
		);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute("Position", VertexArray.VERTCIES_INDEX);
	}

	@Override
	protected void getCustomUniformLocations() {
		color = new Uniform4f(this, "color");
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
	public void setColor(float r, float g, float b, float a) { color.set(r,g,b,a); }
}
