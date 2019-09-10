package gloop.general.exceptions;

import gloop.graphics.rendering.shading.ShaderCompilationException;

public class CurcularReferenceException extends ShaderCompilationException {
	public CurcularReferenceException(String message) { super(message); }
	public CurcularReferenceException(String message, Throwable innerexception) {
		super(message, innerexception);
	}
}
