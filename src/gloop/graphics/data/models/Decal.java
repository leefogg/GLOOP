package gloop.graphics.data.models;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.rendering.shading.materials.DecalMaterial;
import gloop.graphics.rendering.texturing.Texture;

import java.io.IOException;

public class Decal extends Model3D {
	private static VertexArray Model;
	static {
		try {
			Geometry geometry = ModelFactory.loadGeometry("res/_SYSTEM/Models/cube.obj");
			Model = new VertexArray("System_DecalCube", geometry);
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
		super(Model, new DecalMaterial(albedo));
		if (specular != null)
			((DecalMaterial)getMaterial()).setSpecularTexture(specular);
	}
}
