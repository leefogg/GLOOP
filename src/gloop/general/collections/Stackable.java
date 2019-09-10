package gloop.general.collections;

public interface Stackable {
	/**
	 * Called when this item has been pushed onto the top of the stack for the first time
	 */
	void pushed();

	/**
	 * Called when this item leaves the stack
	 */
	void poped();

	/**
	 * Called when this item becomes the top of the stack again
	 */
	void enable();

	/**
	 * Called when this item no longer becomes the top of the stack
	 */
	void disable();
}
