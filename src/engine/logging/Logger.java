package engine.logging;

import java.io.IOException;
import java.util.Timer;

public final class Logger {
	private static final Timer Timer = new Timer();
	private static MemoryLogger MemoryLogger;

	public static final void resourceUnloaded(String message) {

	}

	public static final void error(String message) {

	}

	public static void enableTextureUnitLog() {

	}
	public static void disableTextureUnitLog() {

	}

	public static void enableResourceLog() {

	}
	public static void disableResourceLog() {

	}

	public static void enableErrorLog() {

	}
	public static void disableErrorLog() {

	}


	public static void enableMemoryLog(int interval) {
		try {
			MemoryLogger = new MemoryLogger("C:\\Users\\Lee\\GamesWorkspace\\GLOOP\\Memory.csv");
			Timer.schedule(MemoryLogger, 0, interval);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void disableMemoryLog() {
		if (MemoryLogger == null)
			return;

		MemoryLogger.cancel();
		try {
			MemoryLogger.dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final void dispose() {
		if (MemoryLogger != null)
			disableMemoryLog();
	}
}
