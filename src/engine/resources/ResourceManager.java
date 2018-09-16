package engine.resources;

import engine.general.Disposable;

import java.util.ArrayList;
import java.util.List;

public final class ResourceManager {
	private static final List<Disposable> disposalQueue = new ArrayList(4);

	public static final void queueDisposal(Disposable object) {
		disposalQueue.add(object);
	}

	public static final void disposePendingObjects() {
		while (disposalQueue.size() != 0) {
			// Dispose last thing added to the queue first,
			// If its adds even more, we'll dispose them next
			int lastitemindex = disposalQueue.size()-1;
			Disposable object = disposalQueue.get(lastitemindex);
			object.dispose();
			disposalQueue.remove(lastitemindex);
		}

		disposalQueue.clear();
	}
}
