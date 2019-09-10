package gloop.graphics.data.models;

import gloop.resources.FileProvider;

public abstract class  ModelFileProvider implements FileProvider<Geometry> {
	private final String supportedExtension;

	public ModelFileProvider(String fileextension) {
		supportedExtension = fileextension;
	}

	public String getSupportedExtension() { return supportedExtension; }
}
