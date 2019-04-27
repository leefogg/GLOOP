package GLOOP.graphics.rendering.shading.materials;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;

public final class SingleColorMaterial extends Material<SingleColorShader> {
	private final Vector4f color = new Vector4f(1,1,1,1);
	private static SingleColorShader shader;

	public SingleColorMaterial() throws IOException {
		this(new Vector4f(1,1,1,1));
	}
	public SingleColorMaterial(java.awt.Color color) throws IOException {
		this(new Vector4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
	}
	public SingleColorMaterial(Vector4f color) throws IOException {
		shader = getShaderSingleton();
		this.color.set(color);
	}

	public static final SingleColorShader getShaderSingleton() throws IOException {
		if (shader == null)
			shader = new SingleColorShader();

		return shader;
	}

	@Override
	public SingleColorShader getShader() {
		return shader;
	}

	@Override
	public void commit() {
		shader.setColor(color);
	}

	@Override
	protected boolean hasTransparency() { return color.w < 1f;	}

	public void setColor(Vector4f color) {
		this.color.set(color);
	}
	public void setColor(Vector3f color) { setColor(color.x, color.y, color.z);}

	public void setColor(float red, float green, float blue) { color.set(red, green, blue);	}
}