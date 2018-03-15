package engine.graphics.models;

import engine.Disposable;
import engine.graphics.data.DataConversion;
import engine.resources.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexArray implements Disposable {
	private boolean isDisposed = false;
	public static final int
	VertciesIndex = 0,
	TextureCoordinatesIndex = 1,
	VertexNormalsIndex = 2,
	VertexTangentsIndex = 3;

	private final String name;
	private final int ID = GL30.glGenVertexArrays();
	private final VertexBuffer[] VBOs = new VertexBuffer[5];
	private int NumberOfIndices, NumberofVertcies;
	public RenderMode renderMode = RenderMode.Triangles;

	public VertexArray(String name) {
		this.name = name;
		System.out.println("Created " + this);
		VertexArrayManager.register(this);
	}
	public VertexArray(String name, Geometry mesh) {
		this(name);

		storeMesh(mesh);
	}

	public int getID() {
		return ID;
	}
	String getName() {
		return name;
	}

	public int storeIndicies(int[] indicies) {
		return storeIndicies(DataConversion.toGLBuffer(indicies));
	}
	public int storeIndicies(IntBuffer indicies) {
		NumberOfIndices = indicies.capacity();

		VertexBuffer indiciesarray = new VertexBuffer(GLArrayType.Element);
		indiciesarray.store(indicies, DataType.getSmallest(0, NumberOfIndices));

		return indiciesarray.getID();
	}

	public int storeVertcies(float[] vertcies) {
		return storeVertcies(DataConversion.toGLBuffer(vertcies));
	}
	public int storeVertcies(FloatBuffer data) {
		NumberofVertcies = data.capacity() / 3;

		VertexBuffer verciesarray = new VertexBuffer(GLArrayType.Array);
		verciesarray.store(data);

		bindAttribute(verciesarray,	VertciesIndex, 3);

		return verciesarray.getID();
	}

	public int storeTextureCoords(float[] texturecoords) {
		return storeTextureCoords(DataConversion.toGLBuffer(texturecoords));
	}
	public int storeTextureCoords(FloatBuffer texturecoordinates) {
		VertexBuffer texturecoordinatesarray = new VertexBuffer(GLArrayType.Array);
		texturecoordinatesarray.store(texturecoordinates);

		bindAttribute(texturecoordinatesarray, TextureCoordinatesIndex, 2);

		return texturecoordinatesarray.getID();
	}

	public int storeNormals(float[] normals) {
		return storeNormals(DataConversion.toGLBuffer(normals));
	}
	public int storeNormals(FloatBuffer normals) {
		VertexBuffer normalsarray = new VertexBuffer(GLArrayType.Array);
		normalsarray.store(normals);

		bindAttribute(normalsarray, VertexNormalsIndex, 3);

		return normalsarray.getID();
	}

	public int storeTangents(float[] tangents) {
		return storeNormals(DataConversion.toGLBuffer(tangents));
	}
	public int storeTangents(FloatBuffer tangents) {
		VertexBuffer tangentsarray = new VertexBuffer(GLArrayType.Array);
		tangentsarray.store(tangents);

		bindAttribute(tangentsarray, VertexTangentsIndex, 3);

		return tangentsarray.getID();
	}

	public void storeAll(float[] vertcies, float[] texturecoords, float[] normals, float[] tangents) {
		int arraysize = vertcies.length + texturecoords.length + normals.length + tangents.length;
		int stridesize = 11;
		FloatBuffer data = BufferUtils.createFloatBuffer(arraysize);
		int
		vertciesoffset = 0,
		textureoffset = 0,
		normalsoffset = 0,
		tangentoffset = 0;
		for (int i=0; i<arraysize; i += stridesize) {
			data.put(vertcies[vertciesoffset++]); // Vertex X
			data.put(vertcies[vertciesoffset++]); // Vertex Y
			data.put(vertcies[vertciesoffset++]); // Vertex Z

			data.put(texturecoords[textureoffset++]); // Texture U
			data.put(texturecoords[textureoffset++]); // Texture V

			data.put(normals[normalsoffset++]); // Normal X
			data.put(normals[normalsoffset++]); // Normal Y
			data.put(normals[normalsoffset++]); // Normal Z

			data.put(tangents[tangentoffset++]); // Tangent X
			data.put(tangents[tangentoffset++]); // Tangent Y
			data.put(tangents[tangentoffset++]); // Tangent Z
		}

		data.flip();
		storeStripped(data, true, true, true);
	}

	public void storeMesh(Geometry mesh) {
		storeAll(mesh.getVertcesArray(), mesh.getTextureCoordinatesArray(), mesh.getFaceNormalsArray(), mesh.getTangentsArray());
		storeIndicies(mesh.getIndexBuffer());
	}
	public void storeStripped(float[] data, boolean textureprovided, boolean normalprovided, boolean tangentsprovided) {
		storeStripped(DataConversion.toGLBuffer(data), textureprovided, normalprovided, tangentsprovided);
	}
	public void storeStripped(FloatBuffer data, boolean uvsprovided, boolean normalprovided, boolean tangentsprovided) { // TODO: Derive from passed Geo object
		VertexBuffer strippedbuffer = new VertexBuffer(GLArrayType.Array);
		strippedbuffer.store(data);

		int
		offset = 0,
		size = 3;
		if (uvsprovided)
			size += 2;
		if (normalprovided)
			size += 3;
		if (tangentsprovided)
			size += 3;

		bindAttribute(strippedbuffer, VertciesIndex, 				3, size, offset, false); 	// bind vertices
		offset += 3;
		if (uvsprovided) {
			bindAttribute(strippedbuffer, TextureCoordinatesIndex,  2, size, offset, false); 	// bind texture coordinates
			offset += 2;
		}
		if (normalprovided) {
			bindAttribute(strippedbuffer, VertexNormalsIndex, 		3, size, offset, false); 	// bind face normals
			offset += 3;
		}
		if (tangentsprovided) {
			bindAttribute(strippedbuffer, VertexTangentsIndex, 		3, size, offset, false); 	// bind tangents
			offset += 3;
		}

		NumberofVertcies = data.capacity() / size;
	}

	public void bindAttribute(VertexBuffer vbo, int index, int datawidth) {
		bindAttribute(vbo, index, datawidth, 0, 0, false);
	}
	public void bindAttribute(VertexBuffer vbo, int index, int datawidth, int stride, int offset, boolean instanced) {
		if (!bind())
			return;

		int datatypeinbytes = vbo.getDataType().getSize();
		vbo.bindAttribute(index, false, datawidth, stride*datatypeinbytes, offset*datatypeinbytes);
		if (instanced)
			setAttributeInstaced(index);
		GL20.glEnableVertexAttribArray(index);

		VBOs[index] = vbo;
	}

	public void enable() {
		bind();
	}
	public void disable() {
		unbind();
	}

	private void setAttributeInstaced(int index) {
		setAttributeInstaced(index, 1);
	}
	private void setAttributeInstaced(int index, int divisor) {
		GL33.glVertexAttribDivisor(index, divisor);
	}

	public void enableAllAttributes() {
		if (!bind())
			return;

		for (int i=0; i<VBOs.length; i++) {
			if (VBOs[i] == null)
				return;

			GL20.glEnableVertexAttribArray(i);
		}
	}
	public void disableAllAttributes() {
		if (!bind())
			return;

		for (int i=0; i<VBOs.length; i++) {
			if (VBOs[i] == null)
				return;

			GL20.glDisableVertexAttribArray(i);
		}
	}

	public void setRenderingMode(RenderMode rendermode) {
		this.renderMode = rendermode;
	}

	public boolean bind() {
		if (isDisposed())
			return false;

		GL30.glBindVertexArray(ID);
		return true;
	}

	public void render() {
		if (!bind())
			return;

		if (isIndexed())
			GL11.glDrawElements(renderMode.getGLType(), NumberOfIndices, DataType.UInteger.getGLEnum(), 0);
		else
			GL11.glDrawArrays(renderMode.getGLType(), 0, NumberofVertcies);
	}
	public void unbind() {
		GL30.glBindVertexArray(0);
	}

	private final boolean isIndexed() {
		return NumberOfIndices != 0;
	}

	@Override
	public void dispose() {
		if (isDisposed())
			return;

		System.out.println("Deleting VAO ID" + ID);

		GL30.glDeleteVertexArrays(ID);
		VertexArrayManager.unregister(this);
	}

	@Override
	public void requestDisposal() {
		ResourceManager.queueDisposal(this);
	}

	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	public String toString() {
		return "VAO: ID: "+ID + ", Name: " + name;
	}
}
