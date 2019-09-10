package gloop.graphics.data;

import gloop.resources.Disposable;
import gloop.resources.ResourceManager;

public abstract class Buffer implements Disposable {
	protected long length;

	private boolean disposed = false;

	public Buffer(long size) {
		this.length = size;

		alloc(size);
	}

	protected abstract void alloc(long size);

	protected abstract void update(long size);

	@Override
	public void requestDisposal() {	ResourceManager.queueDisposal(this);	}

	@Override
	public void dispose() {
		disposed = true;
	}

	public boolean isDisposed() {
		return disposed;
	}

	public long getSizeInBytes() { return length; }
}
