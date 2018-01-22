package engine.general;

import java.util.Arrays;

public class List<E> {
	private static final int DEFAULT_SIZE = 1;
	private static final Object[] EMPTY_DATA = {};

	private final boolean
	removeGaps = false,
	TrimTrailingNulls = false;
	private final int
	incrementationSize = 5;

	/**
     * Shared empty array instance used for empty instances.
     */
    private transient Object[] elements;
    private int size = 0;


    public List() {
    	this(DEFAULT_SIZE);
	}
    public List(int initialCapacity) {
    	 if (initialCapacity > 0) {
             this.elements = new Object[initialCapacity];
         } else if (initialCapacity == 0) {
             this.elements = EMPTY_DATA;
         } else {
             throw new IllegalArgumentException("Illegal Capacity: "+ initialCapacity);
         }
    }

    public void add(E element) {
    	ensureCapacity(size + 1);
    	elements[++size] = element;
    }

    private void ensureCapacity(int size) {
    	if (size < this.size)
    		return;

    	grow(size);
    }

    private void grow(int size) {
    	elements = Arrays.copyOf(elements, size);
    }

    @SuppressWarnings("unchecked")
	public E get(int index) {
    	rangeCheck(index);
    	return (E)elements[index];
    }

    /**
     * Checks if the given index is in range.  If not, throws an appropriate
     * runtime exception.  This method does *not* check if the index is
     * negative: It is always used immediately prior to an array access,
     * which throws an ArrayIndexOutOfBoundsException if index is negative.
     */
    private void rangeCheck(int index) {
        if (index >= size)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }

    /**
     * Constructs an IndexOutOfBoundsException detail message.
     */
    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size;
    }

    /**
     * Removes all of the elements from this list.  The list will
     * be empty after this call returns.
     */
    public void clear() {
        elements = EMPTY_DATA;

        size = 0;
    }


    public int getSize() {
    	return size;
    }

    public int getCapacity() {
    	return elements.length;
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }
}
