package gloop.logging;

import java.io.IOException;
import java.util.Timer;

public abstract class Logger {
	private static final Timer TIMER = new Timer();
	private static MemoryLogger MemoryLogger;

	public static void resourceUnloaded(String message) {

	}

	public static void error(String message) {

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
			MemoryLogger = new MemoryLogger("C:\\Users\\Lee\\GamesWorkspace\\gloop\\Memory.csv");
			TIMER.schedule(MemoryLogger, 0, interval);
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

	public static void dispose() {
		if (MemoryLogger != null)
			disableMemoryLog();
	}
}
