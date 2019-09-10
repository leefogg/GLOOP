package gloop.graphics.data.models;

public class Vertex {
	int
		positionIndex,
		textureCoordinateIndex,
		normalIndex,
		tangentIndex; // Calculated then updated

	//TODO: Contain all attributes here
	public Vertex(int positionindex, int texturecoordinateindex, int normalindex) {
		positionIndex = positionindex;
		textureCoordinateIndex = texturecoordinateindex;
		normalIndex = normalindex;
	}
}
