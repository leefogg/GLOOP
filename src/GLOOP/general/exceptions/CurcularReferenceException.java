package GLOOP.general.exceptions;

import GLOOP.graphics.rendering.shading.ShaderCompilationException;

public class CurcularReferenceException extends ShaderCompilationException {
	public CurcularReferenceException(String message) { super(message); }
	public CurcularReferenceException(String message, Throwable innerexception) {
		super(message, innerexception);
	}
}
