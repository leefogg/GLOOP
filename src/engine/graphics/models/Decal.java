package engine.graphics.models;

import engine.graphics.shading.materials.DecalMaterial;
import engine.graphics.textures.Texture;

import java.io.IOException;

public class Decal extends Model3D {
	private static VertexArray model;
	static {
		try {
			Geometry geometry = new Geometry("res/models/cube.obj");
			model = new VertexArray("System_DecalCube", geometry);
		} catch (IOException e) {
			// TODO: Either load something else or crash
			e.printStackTrace();
		}
	}

	public Decal(Texture albedo) throws IOException {
		this(albedo, null);
	}
	public Decal(Texture albedo, Texture specular) throws IOException {
		super(model, new DecalMaterial(albedo));
		if (specular != null)
			((DecalMaterial)getMaterial()).setSpecularTexture(specular);
	}
}
