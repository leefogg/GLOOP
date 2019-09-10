package gloop.graphics.rendering.shading.materials;

import org.lwjgl.util.vector.ReadableVector4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.IOException;

public final class SingleColorMaterial extends Material<SingleColorShader> {
	private static SingleColorShader Shader;
	private final Vector4f color = new Vector4f(1,1,1,1);

	public SingleColorMaterial() throws IOException {
		this(new Vector4f(1,1,1,1));
	}
	public SingleColorMaterial(java.awt.Color color) throws IOException {
		this(new Vector4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()));
	}
	public SingleColorMaterial(ReadableVector4f color) throws IOException {
		Shader = getShaderSingleton();
		this.color.set(color);
	}

	public static SingleColorShader getShaderSingleton() throws IOException {
		if (Shader == null)
			Shader = new SingleColorShader();

		return Shader;
	}

	@Override
	public SingleColorShader getShader() {
		return Shader;
	}

	@Override
	public void commit() {
		Shader.setColor(color);
	}

	@Override
	protected boolean hasTransparency() { return color.w < 1f;	}

	public void setColor(ReadableVector4f color) {
		this.color.set(color);
	}
	public void setColor(Vector3f color) { setColor(color.x, color.y, color.z);}

	public void setColor(float red, float green, float blue) { color.set(red, green, blue);	}

	@Override
	public boolean supportsShadowMaps() { return true; }
}
