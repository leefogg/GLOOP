package GLOOP.graphics.data.models;

import GLOOP.general.exceptions.UnsupportedException;
import GLOOP.graphics.rendering.shading.materials.DecalMaterial;
import GLOOP.graphics.rendering.texturing.Texture;

import java.io.IOException;

public class Decal extends Model3D {
	private static VertexArray model;
	static {
		try {
			Geometry geometry = ModelFactory.loadGeometry("res/_SYSTEM/Models/cube.obj");
			model = new VertexArray("System_DecalCube", geometry);
		} catch (IOException e) {
			// TODO: Either load something else or crash
			e.printStackTrace();
		} catch (UnsupportedException e) {
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
