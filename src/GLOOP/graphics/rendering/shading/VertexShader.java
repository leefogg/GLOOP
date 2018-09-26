package GLOOP.graphics.rendering.shading;

public class VertexShader extends Shader {
	public VertexShader(String sourcecode) throws ShaderCompilationException {
		super(sourcecode, Shader.Type.Vertex);
	}
}
