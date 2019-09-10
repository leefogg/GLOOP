package gloop.graphics.rendering.shading;

public class VertexShader extends Shader {
	public VertexShader(CharSequence sourcecode) throws ShaderCompilationException {
		super(sourcecode, Shader.Type.Vertex);
	}
}
