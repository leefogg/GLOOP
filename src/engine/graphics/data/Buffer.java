package engine.graphics.data;

import engine.general.Disposable;
import engine.resources.ResourceManager;

public abstract class Buffer implements Disposable {
	protected long Size;

	private boolean disposed = false;

	public Buffer(long size) {
		this.Size = size;

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

	public long getSizeInBytes() { return Size; }
}
