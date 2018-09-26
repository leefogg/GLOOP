package GLOOP.resources;

import java.io.IOException;

public interface FileProvider<T> {
	T get(String filepath) throws IOException;
}
