package gloop.general.exceptions;

public final class ObjectDisposedException extends RuntimeException {
	private static final long serialVersionUID = -6278625057130567070L;

	public ObjectDisposedException() {}

	public ObjectDisposedException(String message) {
		super(message);
	}
}
