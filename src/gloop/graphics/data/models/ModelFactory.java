package gloop.graphics.data.models;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.physics.data.AABB;
import gloop.resources.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelFactory {
	private static class GeometryCacheModel {
		public VertexArray vao;
		public AABB boundingBox;

		public GeometryCacheModel(VertexArray vao, AABB boundingBox) {
			this.vao = vao;
			this.boundingBox = boundingBox;
		}
	}
	private static final List<GeometryCacheModel> CACHE = new ArrayList<>();

	private static final Map<String, ModelFileProvider> FILE_PROVIDERS = new HashMap<>(2);

	static {
		addFileProvider(new gloop.graphics.data.models.OBJModelFileProvider());
	}

	public static Model3D getModel(String filepath, Material material) throws IOException, UnsupportedException {
		Model3D cachedmodel = createModelFromCache(filepath, material);
		if (cachedmodel != null)
			return cachedmodel;

		// Wasn't in cache, so load it
		System.out.println("First load. Returning new.");

		getModelVAO(filepath);
		// And now it'll be in cache so return it
		return createModelFromCache(filepath, material);
	}

	private static Model3D createModelFromCache(CharSequence filepath, Material material) {
		GeometryCacheModel cachedmodel = getModelFromCache(filepath);
		if (cachedmodel != null)
			return createModel(cachedmodel, material);
		return null;
	}

	private static VertexArray getModelVAO(String filepath) throws IOException, UnsupportedException {
		Geometry geo = loadGeometry(filepath);
		VertexArray newvao = new VertexArray(filepath); // Saved automatically
		newvao.storeMesh(geo);

		registerModel(newvao, geo.getBoundingBox());

		return newvao;
	}

	private static GeometryCacheModel getModelFromCache(CharSequence path) {
		// This method will only be called when we just added a new model
		// so most likely will be at the end, so search in last to first order
		for (int i = CACHE.size()-1; i>=0; i--) {
			GeometryCacheModel model = CACHE.get(i);
			if (model.vao.getName().contentEquals(path))
				return model;
		}

		return null;
	}

	private static void registerModel(VertexArray vao, AABB boundingbox) {
		CACHE.add(new GeometryCacheModel(vao, boundingbox));
	}

	private static Model3D createModel(GeometryCacheModel model, Material material) {
		return new Model3D(model.vao, material, model.boundingBox);
	}

	static Geometry loadGeometry(String filepath) throws UnsupportedException, IOException {
		String extension = File.getFileExtension(filepath);
		ModelFileProvider provider = FILE_PROVIDERS.get(extension);
		if (provider == null)
			throw new UnsupportedException("Model provider for file extension " + extension + " could not be found.");

		return provider.get(filepath);
	}

	public static void addFileProvider(ModelFileProvider modelprovider) { FILE_PROVIDERS.put(modelprovider.getSupportedExtension(), modelprovider); }
}
