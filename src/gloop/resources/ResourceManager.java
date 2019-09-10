package gloop.resources;

import java.util.ArrayList;
import java.util.List;

public abstract class ResourceManager {
	private static final List<Disposable> DISPOSAL_QUEUE = new ArrayList(4);

	public static void queueDisposal(Disposable object) {
		DISPOSAL_QUEUE.add(object);
	}

	public static void disposePendingObjects() {
		while (DISPOSAL_QUEUE.size() != 0) {
			// Dispose last thing added to the queue first,
			// If its adds even more, we'll dispose them next
			int lastitemindex = DISPOSAL_QUEUE.size()-1;
			Disposable object = DISPOSAL_QUEUE.get(lastitemindex);
			object.dispose();
			DISPOSAL_QUEUE.remove(lastitemindex);
		}

		DISPOSAL_QUEUE.clear();
	}
}
