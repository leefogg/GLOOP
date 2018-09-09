package engine.general;

import java.io.IOException;

public interface FileProvider<T> {
	T get(String filepath) throws IOException;
}
