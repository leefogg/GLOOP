package gloop.general;

public class Lazy<T> implements Readable<T>, Expirable {
	private T item;
	private Updater<T> delegate;

	public Lazy(Updater<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public T get() {
		if (item == null)
			item = delegate.update(item);

		return item;
	}

	@Override
	public void expire() {
		item = null;
	}

	public void set(T item) { this.item = item; }
}
