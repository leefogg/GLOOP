package engine.resources;

import engine.Disposable;

import java.util.ArrayList;
import java.util.List;

public final class ResourceManager {
	private static final List<Disposable> disposalQueue = new ArrayList(4);

	public static final void queueDisposal(Disposable object) {
		disposalQueue.add(object);
	}

	public static final void disposePendingObjects() {
		for (Disposable object : disposalQueue)
			object.dispose();

		disposalQueue.clear();
	}
}
