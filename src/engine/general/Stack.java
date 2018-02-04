package engine.general;

public class Stack<E extends Stackable>  {
	private java.util.Stack<E> backingStack = new java.util.Stack();

	public void push(E element) {
		if (!backingStack.isEmpty()) {
			E lastelement = backingStack.peek();
			lastelement.disable();
		}

		backingStack.push(element);
		element.pushed();
	}

	public E pop() {
		if (backingStack.isEmpty())
			return null;

		E element = backingStack.pop();
		element.poped();

		if (!backingStack.isEmpty())
			backingStack.peek().enable();

		return element;
	}

	public E peek() {
		return backingStack.peek();
	}

	public void empty() {
		for (E element : backingStack)
			element.poped();

		backingStack.empty();
	}

	public int getSize() {
		return backingStack.size();
	}
}
