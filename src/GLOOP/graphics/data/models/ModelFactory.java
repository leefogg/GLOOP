package GLOOP.graphics.data.models;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.rendering.shading.materials.Material;
import GLOOP.physics.data.AABB;
import GLOOP.resources.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ModelFactory {
	private static class GeometryCacheModel {
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
		addFileProvider(new GLOOP.graphics.data.models.OBJModelFileProvider());
	}

	public static final Model3D getModel(String filepath, Material material) throws IOException, UnsupportedException {
		Model3D cachedmodel = createModelFromCache(filepath, material);
		if (cachedmodel != null)
			return cachedmodel;

		// Wasn't in cache, so load it
		System.out.println("First load. Returning new.");

		getModelVAO(filepath);
		// And now it'll be in cache so return it
		return createModelFromCache(filepath, material);
	}

	private static Model3D createModelFromCache(String filepath, Material material) {
		GeometryCacheModel cachedmodel = getModelFromCache(filepath);
		if (cachedmodel != null)
			return createModel(cachedmodel, material);
		return null;
	}

	private static final VertexArray getModelVAO(String filepath) throws IOException, UnsupportedException {
		Geometry geo = loadGeometry(filepath);
		VertexArray newvao = new VertexArray(filepath); // Saved automatically
		newvao.storeMesh(geo);

		registerModel(newvao, geo.getBoundingBox());

		return newvao;
	}

	private static GeometryCacheModel getModelFromCache(String path) {
		// This method will only be called when we just added a new model
		// so most likely will be at the end, so search in last to first order
		for (int i=Cache.size()-1; i>=0; i--) {
			GeometryCacheModel model = Cache.get(i);
			if (model.vao.getName().contentEquals(path))
				return model;
		}

		return null;
	}

	private static final void registerModel(VertexArray vao, AABB boundingbox) {
		Cache.add(new GeometryCacheModel(vao, boundingbox));
	}

	private static final Model3D createModel(GeometryCacheModel model, Material material) {
		return new Model3D(model.vao, material, model.boundingBox);
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
