package engine.graphics.data;

import engine.Disposable;
import engine.resources.ResourceManager;

public abstract class Buffer implements Disposable {
	protected long size;

	private boolean disposed = false;

	public Buffer(long size) {
		this.size = size;

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
}
