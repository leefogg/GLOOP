package engine.graphics.models;

public class Vertex {
	int
	PositionIndex,
	TextureCoordinateIndex,
	NormalIndex,
	TangentIndex; // Calculated then updated

	//TODO: Contain all attributes here
	public Vertex(int positionIndex, int textureCoordinateIndex, int normalIndex) {
		PositionIndex = positionIndex;
		TextureCoordinateIndex = textureCoordinateIndex;
		NormalIndex = normalIndex;
	}
}
