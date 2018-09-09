package engine.graphics.models;

import engine.general.FileProvider;

import java.io.IOException;

public abstract class  ModelFileProvider implements FileProvider<Geometry> {
	private final String supportedExtension;

	public ModelFileProvider(String fileextension) {
		supportedExtension = fileextension;
	}

	public abstract Geometry get(String filepath) throws IOException;

	public String getSupportedExtension() { return supportedExtension; }
}
