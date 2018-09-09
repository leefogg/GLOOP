package engine.graphics.models;

import engine.general.exceptions.UnsupportedException;
import engine.graphics.shading.materials.Material;
import engine.physics.data.AABB;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ModelFactory {
	static class GeometryCacheModel {
		public VertexArray vao;
		public AABB boundingBox;

		public GeometryCacheModel(VertexArray vao, AABB boundingBox) {
			this.vao = vao;
			this.boundingBox = boundingBox;
		}
	}
	private static final ArrayList<GeometryCacheModel> Cache = new ArrayList<>();

	private static final HashMap<String, ModelFileProvider> FileProviders = new HashMap<>(2);

	static {
		addFileProvider(new engine.graphics.models.OBJModelFileProvider());
	}

	public static final Model3D getModel(String filepath, Material material) throws IOException, UnsupportedException {
		Model3D cachedmodel = getModelFromCache(filepath, material);
		if (cachedmodel != null)
			return cachedmodel;

		// None found
		System.out.println("First load. Returning new.");
		// Load
		Geometry geo = loadGeometry(filepath);
		VertexArray newvao = new VertexArray(filepath); // Saved automatically
		newvao.storeMesh(geo);

		registerModel(newvao, geo.getBoundingBox());

		return getModelFromCache(filepath, material);
	}

	private static final void registerModel(VertexArray vao, AABB boundingbox) {
		Cache.add(new GeometryCacheModel(vao, boundingbox));
	}

	private static Model3D getModelFromCache(String path, Material material) {
		// This method will only be called when we just added a new model
		// so most likely will be at the end, so search in last to first order
		for (int i=Cache.size()-1; i>=0; i--) {
			GeometryCacheModel model = Cache.get(i);
			if (model.vao.getName().contentEquals(path))
				return new Model3D(model.vao, material, model.boundingBox);
		}

		return null;
	}

	static Geometry loadGeometry(String filepath) throws UnsupportedException, IOException {
		String extension = File.getFileExtension(filepath);
		ModelFileProvider provider = FileProviders.get(extension);
		if (provider == null)
			throw new UnsupportedException("Model provider for file extension " + extension + " could not be found.");

		return provider.get(filepath);
	}

	public static void addFileProvider(ModelFileProvider modelprovider) { FileProviders.put(modelprovider.getSupportedExtension(), modelprovider); }
}
