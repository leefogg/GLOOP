package GLOOP.resources;

public interface Disposable { // TODO: Use this more! Register all the things.
	void requestDisposal();
	boolean isDisposed();
	void dispose(); // TODO: Return true if success, could add more into queue
	// TODO: Add event to call listeners when disposed
}
