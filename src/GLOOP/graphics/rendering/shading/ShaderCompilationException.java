package GLOOP.graphics.rendering.shading;

public class ShaderCompilationException extends RuntimeException {
	private static final long serialVersionUID = 3333981471775622185L;

	public ShaderCompilationException(String message) { super(message); }
	public ShaderCompilationException(String message, Throwable innerexception) {
		super(message, innerexception);
	}
}
