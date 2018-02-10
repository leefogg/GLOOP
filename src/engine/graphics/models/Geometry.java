package engine.graphics.models;

import engine.physics.data.AABB;
import engine.graphics.data.DataConversion;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.IntBuffer;
import java.util.ArrayList;

public class Geometry {
	private class Face {
		int
		v1,
		v2,
		v3;

		public Face(int v1, int v2, int v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}

		public Vector3f getFaceCenter(Vector3f v1, Vector3f v2, Vector3f v3) {
			Vector3f center = new Vector3f();
			center.set(v1);
			Vector3f.add(center, v2, center);
			Vector3f.add(center, v3, center);
			center.scale(1f / 3f); // x0.3 == /3

			return center;
		}

		public Vector3f getFaceNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
			Vector3f
			u = new Vector3f(),
			v = new Vector3f(),
			normal = new Vector3f();
			Vector3f.sub(v2, v1, u);
			Vector3f.sub(v3, v1, v);
			normal.x = u.y * v.z - u.z * v.y;
			normal.y = u.z * v.x - u.x * v.z;
			normal.z = u.x * v.y - u.y * v.x;

			normal.normalise();
			return normal;
		}
	}

	private final Face[] faces;
	private final Vertex[] vertcies;
	private final Vector3f[] positions, normals;
	private Vector3f[] tangents;
	private final Vector2f[] texturecoordinates;

	@SuppressWarnings("boxing")
	public Geometry(String filepath) throws IOException {
		BufferedReader objfilereader = new BufferedReader(new FileReader(filepath));
		while(!objfilereader.ready()){}

		ArrayList<Vector3f> positions = new ArrayList<>();
		ArrayList<Vector3f> normals = new ArrayList<>();
		ArrayList<Vector2f> texturecoordinates = new ArrayList<>();
		ArrayList<Vertex> vertcies = new ArrayList<>();
		ArrayList<Face> faces = new ArrayList<>();

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

						indicies[i] = vertexpointer; // Add vertex indexes

						if (!subcomponents[1].isEmpty()) { // Is it not blank?
							int uvpointer = Integer.valueOf(subcomponents[1]) - 1;
							indicies[i+3] = uvpointer; // Add UV indexes
						}

						if (!subcomponents[2].isEmpty()) { // Is it not blank?
							int normalpointer = Integer.valueOf(subcomponents[2]) - 1;
							indicies[i+6] = normalpointer; // Add Normal indexes
						}

						i++;
					}
				}

				vertcies.add(
							new Vertex(
								indicies[0],
								indicies[3],
								indicies[6]
							)
						);
				vertcies.add(
						new Vertex(
							indicies[1],
							indicies[4],
							indicies[7]
						)
					);
				vertcies.add(
						new Vertex(
							indicies[2],
							indicies[5],
							indicies[8]
						)
					);

				int numverts = vertcies.size();
				faces.add(
						new Face(
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
		this.positions = new Vector3f[positions.size()];
		positions.toArray(this.positions);

		this.normals = new Vector3f[normals.size()];
		normals.toArray(this.normals);

		this.texturecoordinates = new Vector2f[texturecoordinates.size()];
		texturecoordinates.toArray(this.texturecoordinates);

		this.vertcies = new Vertex[vertcies.size()];
		vertcies.toArray(this.vertcies);

		this.faces = new Face[faces.size()];
		faces.toArray(this.faces);

		normalizeNormals();
		flipUVsVertically();
		calculateTangents();
	}

	private void calculateTangents() {
		tangents = new Vector3f[vertcies.length];

		int i=0;
		for (Face face : faces) {
			Vertex[] vertcies = new Vertex[] { this.vertcies[face.v1], this.vertcies[face.v2], this.vertcies[face.v3]};
			Vector3f
				pos1 = positions[vertcies[0].PositionIndex],
				pos2 = positions[vertcies[1].PositionIndex],
				pos3 = positions[vertcies[2].PositionIndex];
			Vector2f
				uv1 = texturecoordinates[vertcies[0].TextureCoordinateIndex],
				uv2 = texturecoordinates[vertcies[1].TextureCoordinateIndex],
				uv3 = texturecoordinates[vertcies[2].TextureCoordinateIndex];

			// Calculate tangent
			Vector3f
			tangent = new Vector3f(),
			edge1 = new Vector3f(),
			edge2 = new Vector3f();
			Vector2f
				deltaUV1 = new Vector2f(),
				deltaUV2 = new Vector2f();
			Vector3f.sub(pos2, pos1, edge1);
			Vector3f.sub(pos3, pos1, edge2);
			Vector2f.sub(uv2, uv1, deltaUV1);
			Vector2f.sub(uv3, uv1, deltaUV2);

			float f = 1.0f / (deltaUV1.x * deltaUV2.y - deltaUV2.x * deltaUV1.y);
			tangent.x = f * (deltaUV2.y * edge1.x - deltaUV1.y * edge2.x);
			tangent.y = f * (deltaUV2.y * edge1.y - deltaUV1.y * edge2.y);
			tangent.z = f * (deltaUV2.y * edge1.z - deltaUV1.y * edge2.z);
			tangent.normalise();

			// Update pointers
			for (Vertex v : vertcies) {
				// Because vertices are shared, if this vertex's tangent has been calculated already, the new one should be based on the old one
				if (v.TangentIndex != 0) { // Has tangents already
					Vector3f oldtangent = tangents[v.TangentIndex];

					// Calculate average
					Vector3f.add(tangent, oldtangent, tangent);
					tangent.normalise();

					// Update old tangent
					tangents[v.TangentIndex] = tangent;
				} else { // Needs new tangents
					// Store tangent and bitangent
					tangents[i] = tangent;

					v.TangentIndex = i;

					i++;
				}
			}
		}
	}

	public int getNumberOfIndices() {
		return faces.length*3;
	}

	private void normalizeNormals() {
		for (Vector3f normal : normals)
			if (normal.lengthSquared() > Float.MIN_VALUE)
				normal.normalise();
	}

	public void invertNormals() {
		for (Vector3f normal : normals)
			normal.scale(-1);
	}

	private Vector3f calulateFaceNormal(Face f) {
		Vector3f
		v1 = positions[vertcies[f.v1].PositionIndex],
		v2 = positions[vertcies[f.v2].PositionIndex],
		v3 = positions[vertcies[f.v3].PositionIndex];

		return f.getFaceNormal(v1, v2, v3);
	}

	public void flipUVsVertically() {
		for (Vector2f uv : texturecoordinates)
			uv.y = 1f - uv.y;
	}

	public void flipUVsHorizontally() {
		for (Vector2f uv : texturecoordinates)
			uv.x = 1f - uv.x;
	}

	public void removeDuplicateVertcies(float precision) {
		//TODO: Write & Implement
	}

	public void normalizeScale() { //TODO: Test
		AABB boundingbox = getBoundingBox();
		float size = boundingbox.getSize().length();

		float scaler = 1f / size; // Could have millions of verts, multiplying is faster than dividing
		for (Vector3f position : positions)
			position.scale(scaler);
	}

	public void centre() { //TODO: Test
		Vector3f centre = getBoundingBox().getCentre();
		for (Vector3f position : positions)
			Vector3f.sub(position, centre, position);
	}

	public AABB getBoundingBox() {
		return new AABB(positions);
	}

	public void translate(Vector3f translation) {
		for (Vector3f position : positions)
			Vector3f.add(position, translation, position);
	}

	public void transform(Matrix4f transformation) { //TODO: Test
		Vector4f temp = new Vector4f();
		for (Vector3f vert : positions) {
			temp.set(vert.x, vert.y, vert.z);
			Matrix4f.transform(transformation, temp, temp);
			vert.set(temp.x, temp.y, temp.z);
		}
		for (Vector3f norm : normals) {
			temp.set(norm.x, norm.y, norm.z);
			Matrix4f.transform(transformation, temp, temp);
			norm.set(temp.x, temp.y, temp.z);
		}
	}

	public Vector3f getCentre() {
		Vector3f centre = new Vector3f();

		for (Vector3f vertex : positions)
			Vector3f.add(centre, vertex, centre);
		centre.scale(1f / positions.length);

		return centre;
	}

	public float[] getVertcesArray() {
		Vector3f[] positions = new Vector3f[faces.length*3];
		int i = 0;
		for (Face f : faces) {
			positions[i++] = this.positions[vertcies[f.v1].PositionIndex];
			positions[i++] = this.positions[vertcies[f.v2].PositionIndex];
			positions[i++] = this.positions[vertcies[f.v3].PositionIndex];
		}

		return DataConversion.toFloatArray(positions);
	}

	public float[] getTextureCoordinatesArray() {
		Vector2f[] texcoords = new Vector2f[faces.length * 3];
		int i = 0;
		for (Face f : faces) {
			texcoords[i++] = texturecoordinates[vertcies[f.v1].TextureCoordinateIndex];
			texcoords[i++] = texturecoordinates[vertcies[f.v2].TextureCoordinateIndex];
			texcoords[i++] = texturecoordinates[vertcies[f.v3].TextureCoordinateIndex];
		}

		return DataConversion.toFloatArray(texcoords);
	}

	public float[] getFaceNormalsArray() {
		Vector3f[] norms = new Vector3f[faces.length*3];
		int i = 0;
		for (Face f : faces) {
			norms[i++] = normals[vertcies[f.v1].NormalIndex];
			norms[i++] = normals[vertcies[f.v2].NormalIndex];
			norms[i++] = normals[vertcies[f.v3].NormalIndex];
		}
		return DataConversion.toFloatArray(norms);
	}

	public float[] getTangentsArray() {
		Vector3f[] tangents = new Vector3f[faces.length * 3];
		int i = 0;
		for (Face f : faces) {
			tangents[i++] = this.tangents[vertcies[f.v1].TangentIndex];
			tangents[i++] = this.tangents[vertcies[f.v2].TangentIndex];
			tangents[i++] = this.tangents[vertcies[f.v3].TangentIndex];
		}

		return DataConversion.toFloatArray(tangents);
	}

	public IntBuffer getIndexBuffer() {
		int[] indicies = new int[faces.length*3];
		int i=0;
		for (Face f : faces) {
			indicies[i++] = f.v1;
			indicies[i++] = f.v2;
			indicies[i++] = f.v3;
		}

		return DataConversion.toGLBuffer(indicies);
	}

	public int getNumberOfVertices() {
		return vertcies.length;
	}

	public boolean hasVertces() {
		return getNumberOfVertices() != 0;
	}

	public int getNumberOfTextureCoordinates() {
		return texturecoordinates.length;
	}

	public boolean hasTextureCoordinates() {
		return getNumberOfTextureCoordinates() != 0;
	}

	public int getNumberOfFaces() {
		return faces.length;
	}

	public boolean hasFaces() {
		return getNumberOfFaces() != 0;
	}

	public void combine(Geometry geometry) {
		//TODO: Implement
	}
	public void deleteDuplicateVertcies() {
		//TODO: Implement
	}
	public void deleteDuplicateUVs() {
		//TODO: Implement
	}

	public void debugUVs(String texture) throws IOException {
		BufferedImage image = ImageIO.read(new FileInputStream(texture));
		int width = image.getWidth();
		int height = image.getHeight();


		java.awt.Graphics canvas = image.createGraphics();
		canvas.setColor(java.awt.Color.white);
		for (Face f : faces) {
			Vector2f va = texturecoordinates[vertcies[f.v1].TextureCoordinateIndex];
			Vector2f vb = texturecoordinates[vertcies[f.v2].TextureCoordinateIndex];
			Vector2f vc = texturecoordinates[vertcies[f.v3].TextureCoordinateIndex];
			canvas.drawLine(
					(int)(va.x*width),
					(int)(va.y*height),
					(int)(vb.x*width),
					(int)(vb.y*height)
			);
			canvas.drawLine(
					(int)(vb.x*width),
					(int)(vb.y*height),
					(int)(vc.x*width),
					(int)(vc.y*height)
			);
			canvas.drawLine(
					(int)(vc.x*width),
					(int)(vc.y*height),
					(int)(va.x*width),
					(int)(va.y*height)
			);
		}
		canvas.dispose();

		//TODO: Detect extension
		ImageIO.write(image, "png", new File(texture));
	}

	public void writeToOBJ(String path) throws IOException {
		BufferedWriter file = new BufferedWriter(new FileWriter(path));

		for (Vector3f position : positions) {
			file.write("v " + position.x + " " + position.y + " " + position.z);
			file.newLine();
		}
		for (Vector2f texturecoordinate : texturecoordinates) {
			file.write("vt " + texturecoordinate.x + " " + texturecoordinate.y);
			file.newLine();
		}
		for (Vector3f normal : normals) {
			file.write("vn " + normal.x + " " + normal.y + " " + normal.z);
			file.newLine();
		}

		for (Face f : faces) {
			Vertex
			v1 = vertcies[f.v1],
			v2 = vertcies[f.v2],
			v3 = vertcies[f.v3];
			file.write("f " + (v1.PositionIndex + 1) + "/" + (v1.TextureCoordinateIndex + 1) + "/" + (v1.NormalIndex + 1));
			file.write(" " + (v2.PositionIndex + 1) + "/" + (v2.TextureCoordinateIndex + 1) + "/" + (v2.NormalIndex + 1));
			file.write(" " + (v3.PositionIndex + 1) + "/" + (v3.TextureCoordinateIndex + 1) + "/" + (v3.NormalIndex + 1));
			file.newLine();
		}

		file.flush();
		file.close();
	}

	public int getSize() {
		int lengtharrays = faces.length*3;
		int totalvertsnormalsandtexcoords = lengtharrays*3;
		int totalindicies = lengtharrays;
		int indiciesbytes = DataType.getSmallest(0, lengtharrays).getSize();
		return totalvertsnormalsandtexcoords*4 + totalindicies*indiciesbytes;
	}
}
