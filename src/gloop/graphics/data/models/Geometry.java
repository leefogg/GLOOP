package gloop.graphics.data.models;

import gloop.graphics.data.DataType;
import gloop.physics.data.AABB;
import gloop.graphics.data.DataConversion;
import org.lwjgl.util.vector.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.IntBuffer;

public class Geometry {
	public static class Face {
		int
		v1,
		v2,
		v3;

		public Face(int v1, int v2, int v3) {
			this.v1 = v1;
			this.v2 = v2;
			this.v3 = v3;
		}

		public Vector3f getFaceCenter(ReadableVector3f v1, Vector3f v2, Vector3f v3) {
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
	public Geometry(Vector3f[] positions, Vector2f[] uvs, Vector3f[] normals, Vertex[] vertcies, Face[] faces) {
		this.positions = positions;
		this.texturecoordinates = uvs;
		this.normals = normals;
		this.vertcies = vertcies;
		this.faces = faces;

		normalizeNormals();
		flipUVsVertically();
		calculateTangents();
	}

	private void calculateTangents() {
		tangents = new Vector3f[faces.length*3];

		int i=0;
		for (Face face : faces) {
			Vertex[] vertcies = new Vertex[] { this.vertcies[face.v1], this.vertcies[face.v2], this.vertcies[face.v3]};
			Vector3f
				pos1 = positions[vertcies[0].positionIndex],
				pos2 = positions[vertcies[1].positionIndex],
				pos3 = positions[vertcies[2].positionIndex];
			Vector2f
				uv1 = texturecoordinates[vertcies[0].textureCoordinateIndex],
				uv2 = texturecoordinates[vertcies[1].textureCoordinateIndex],
				uv3 = texturecoordinates[vertcies[2].textureCoordinateIndex];

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
				if (v.tangentIndex != 0) { // Has tangents already
					Vector3f oldtangent = tangents[v.tangentIndex];

					// Calculate average
					Vector3f.add(tangent, oldtangent, tangent);
					tangent.normalise();

					// Update old tangent
					tangents[v.tangentIndex] = tangent;
				} else { // Needs new tangents
					// Store tangent and bitangent
					tangents[i] = tangent;

					v.tangentIndex = i;

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
		v1 = positions[vertcies[f.v1].positionIndex],
		v2 = positions[vertcies[f.v2].positionIndex],
		v3 = positions[vertcies[f.v3].positionIndex];

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
			positions[i++] = this.positions[vertcies[f.v1].positionIndex];
			positions[i++] = this.positions[vertcies[f.v2].positionIndex];
			positions[i++] = this.positions[vertcies[f.v3].positionIndex];
		}

		return DataConversion.toFloatArray(positions);
	}

	public float[] getTextureCoordinatesArray() {
		Vector2f[] texcoords = new Vector2f[faces.length * 3];
		int i = 0;
		for (Face f : faces) {
			texcoords[i++] = texturecoordinates[vertcies[f.v1].textureCoordinateIndex];
			texcoords[i++] = texturecoordinates[vertcies[f.v2].textureCoordinateIndex];
			texcoords[i++] = texturecoordinates[vertcies[f.v3].textureCoordinateIndex];
		}

		return DataConversion.toFloatArray(texcoords);
	}

	public float[] getFaceNormalsArray() {
		Vector3f[] norms = new Vector3f[faces.length*3];
		int i = 0;
		for (Face f : faces) {
			norms[i++] = normals[vertcies[f.v1].normalIndex];
			norms[i++] = normals[vertcies[f.v2].normalIndex];
			norms[i++] = normals[vertcies[f.v3].normalIndex];
		}
		return DataConversion.toFloatArray(norms);
	}

	public float[] getTangentsArray() {
		Vector3f[] tangents = new Vector3f[faces.length * 3];
		int i = 0;
		for (Face f : faces) {
			tangents[i++] = this.tangents[vertcies[f.v1].tangentIndex];
			tangents[i++] = this.tangents[vertcies[f.v2].tangentIndex];
			tangents[i++] = this.tangents[vertcies[f.v3].tangentIndex];
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
			Vector2f va = texturecoordinates[vertcies[f.v1].textureCoordinateIndex];
			Vector2f vb = texturecoordinates[vertcies[f.v2].textureCoordinateIndex];
			Vector2f vc = texturecoordinates[vertcies[f.v3].textureCoordinateIndex];
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
		ImageIO.write(image, "png", new java.io.File(texture));
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
			file.write("f " + (v1.positionIndex + 1) + "/" + (v1.textureCoordinateIndex + 1) + "/" + (v1.normalIndex + 1));
			file.write(" " + (v2.positionIndex + 1) + "/" + (v2.textureCoordinateIndex + 1) + "/" + (v2.normalIndex + 1));
			file.write(" " + (v3.positionIndex + 1) + "/" + (v3.textureCoordinateIndex + 1) + "/" + (v3.normalIndex + 1));
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
