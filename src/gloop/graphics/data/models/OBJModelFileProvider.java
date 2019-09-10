package gloop.graphics.data.models;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OBJModelFileProvider extends ModelFileProvider {
	public OBJModelFileProvider() {
		super("obj");
	}

	@Override
	public Geometry get(String filepath) throws IOException {
		BufferedReader objfilereader = new BufferedReader(new FileReader(filepath));
		while(!objfilereader.ready()){}

		List<Vector3f> positions = new ArrayList<>();
		List<Vector3f> normals = new ArrayList<>();
		List<Vector2f> texturecoordinates = new ArrayList<>();
		List<Vertex> vertcies = new ArrayList<>();
		List<Geometry.Face> faces = new ArrayList<>();

		String line;
		int linenumber = 1;
		while ((line = objfilereader.readLine()) != null)  {
			line = line.trim();
			line = line.replace("  ", " ");

			if (line.startsWith("v ")) {
				String[] values = line.substring(2).split(" ");
				positions.add(
						new Vector3f(
							Float.valueOf(values[0]),
							Float.valueOf(values[1]),
							Float.valueOf(values[2])
						)
				);
			} else if (line.startsWith("vt ")) {
				String[] values = line.substring(3).split(" ");
				float
						x = Float.valueOf(values[0]),
						y = Float.valueOf(values[1]);

				texturecoordinates.add(new Vector2f(x, y));
			} else if (line.startsWith("vn ")) { //TODO: Replace with calculation at the end of load
				String[] values = line.substring(3).split(" ");
				normals.add(
						new Vector3f(
							Float.valueOf(values[0]),
							Float.valueOf(values[1]),
							Float.valueOf(values[2])
						)
				);
			} else if (line.startsWith("f ")) {
				int[] indicies = new int[9]; {
					String[] components = line.substring(2).split(" ");
					if (components.length > 3)
						throw new IOException("Face on line " + linenumber + " is not triangulated.");

					int i=0;
					for (String component : components) {
						String[] subcomponents = component.split("/");
						int vertexpointer = Integer.valueOf(subcomponents[0]) - 1;

						indicies[i++] = vertexpointer; // Add vertex indexes

						if (!subcomponents[1].isEmpty()) { // Is it not blank?
							int uvpointer = Integer.valueOf(subcomponents[1]) - 1;
							indicies[i++] = uvpointer; // Add UV indexes
						}

						if (!subcomponents[2].isEmpty()) { // Is it not blank?
							int normalpointer = Integer.valueOf(subcomponents[2]) - 1;
							indicies[i++] = normalpointer; // Add Normal indexes
						}
					}
				}

				for (int i=0; i<9; )
					vertcies.add(
							new Vertex(
								indicies[i++],
								indicies[i++],
								indicies[i++]
							)
					);

				int numverts = vertcies.size();
				faces.add(
						new Geometry.Face(
							numverts-3,
							numverts-2,
							numverts-1
						)
				);
			}

			linenumber++;
		}
		objfilereader.close();

		// Convert to lists
		Vector3f[] positionsarray = new Vector3f[positions.size()];
		positions.toArray(positionsarray);

		Vector3f[] normalsarray = new Vector3f[normals.size()];
		normals.toArray(normalsarray);

		Vector2f[] texturecoordinatesarray = new Vector2f[texturecoordinates.size()];
		texturecoordinates.toArray(texturecoordinatesarray);

		Vertex[] vertciesarray = new Vertex[vertcies.size()];
		vertcies.toArray(vertciesarray);

		Geometry.Face[] facesarray = new Geometry.Face[faces.size()];
		faces.toArray(facesarray);

		return new Geometry(
			positionsarray,
			texturecoordinatesarray,
			normalsarray,
			vertciesarray,
			facesarray
		);
	}
}
