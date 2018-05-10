package engine.graphics.models;

import engine.graphics.shading.materials.Material;
import engine.physics.data.AABB;

import java.io.IOException;
import java.util.ArrayList;

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

	public static final Model3D getModel(String path, Material material) throws IOException {
		Model3D cachedmodel = getModelFromCache(path, material);
		if (cachedmodel != null)
			return cachedmodel;

		// None found
		System.out.println("First load. Returning new.");
		// Load
		Geometry geo = new Geometry(path);
		VertexArray newvao = new VertexArray(path); // Saved automatically
		newvao.storeMesh(geo);

		registerModel(newvao, geo.getBoundingBox());

		return getModelFromCache(path, material);
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
}
