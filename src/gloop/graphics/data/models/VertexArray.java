package gloop.graphics.data.models;

import gloop.resources.Disposable;
import gloop.graphics.data.DataConversion;
import gloop.graphics.data.DataType;
import gloop.graphics.rendering.Renderer;
import gloop.resources.ResourceManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class VertexArray implements Disposable {
	public static final int
	VERTCIES_INDEX = 0,
	TEXTURE_COORDINATES_INDEX = 1,
	VERTEX_NORMALS_INDEX = 2,
	VERTEX_TANGENTS_INDEX = 3;

	private final String name;
	private final int ID = GL30.glGenVertexArrays();
	private final VertexBuffer[] vbos = new VertexBuffer[6];
	private int numberOfIndices, numberofVertcies, maxInstances;
	public RenderMode renderMode = RenderMode.Triangles;
	private boolean isDisposed = false;

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
		numberOfIndices = indicies.capacity();

		VertexBuffer indiciesarray = new VertexBuffer(GLArrayType.Element);
		indiciesarray.store(indicies, DataType.getSmallest(0, numberOfIndices));

		return indiciesarray.getID();
	}

	public int storeVertcies(float[] vertcies) {
		return storeVertcies(DataConversion.toGLBuffer(vertcies));
	}
	public int storeVertcies(FloatBuffer data) {
		numberofVertcies = data.capacity() / 3;

		VertexBuffer verciesarray = new VertexBuffer(GLArrayType.Array);
		verciesarray.store(data);

		bindAttribute(verciesarray, VERTCIES_INDEX, 3);

		return verciesarray.getID();
	}

	public int storeTextureCoords(float[] texturecoords) {
		return storeTextureCoords(DataConversion.toGLBuffer(texturecoords));
	}
	public int storeTextureCoords(FloatBuffer texturecoordinates) {
		VertexBuffer texturecoordinatesarray = new VertexBuffer(GLArrayType.Array);
		texturecoordinatesarray.store(texturecoordinates);

		bindAttribute(texturecoordinatesarray, TEXTURE_COORDINATES_INDEX, 2);

		return texturecoordinatesarray.getID();
	}

	public int storeNormals(float[] normals) {
		return storeNormals(DataConversion.toGLBuffer(normals));
	}
	public int storeNormals(FloatBuffer normals) {
		VertexBuffer normalsarray = new VertexBuffer(GLArrayType.Array);
		normalsarray.store(normals);

		bindAttribute(normalsarray, VERTEX_NORMALS_INDEX, 3);

		return normalsarray.getID();
	}

	public int storeTangents(float[] tangents) {
		return storeNormals(DataConversion.toGLBuffer(tangents));
	}
	public int storeTangents(FloatBuffer tangents) {
		VertexBuffer tangentsarray = new VertexBuffer(GLArrayType.Array);
		tangentsarray.store(tangents);

		bindAttribute(tangentsarray, VERTEX_TANGENTS_INDEX, 3);

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
		storeStriped(data, true, true, true);
	}

	public void storeMesh(Geometry mesh) {
		storeAll(mesh.getVertcesArray(), mesh.getTextureCoordinatesArray(), mesh.getFaceNormalsArray(), mesh.getTangentsArray());
		storeIndicies(mesh.getIndexBuffer());
	}
	public void storeStriped(float[] data, boolean textureprovided, boolean normalprovided, boolean tangentsprovided) {
		storeStriped(DataConversion.toGLBuffer(data), textureprovided, normalprovided, tangentsprovided);
	}
	public void storeStriped(FloatBuffer data, boolean uvsprovided, boolean normalprovided, boolean tangentsprovided) { // TODO: Derive from passed Geo object
		int elementcount = 1;
		if (uvsprovided) {
			elementcount++;
			if (normalprovided) {
				elementcount++;
				if (tangentsprovided)
					elementcount++;
			}
		}
		int[] datawidths = new int[elementcount];
		boolean[] isinstanced = new boolean[elementcount];
		datawidths[0] = 3;
		if (uvsprovided) {
			datawidths[1] = 2;
			if (normalprovided) {
				datawidths[2] = 3;
				if (tangentsprovided)
					datawidths[3] = 3;
			}
		}
		storeStriped(data, datawidths, isinstanced, 0);
	}
	public void storeStriped(FloatBuffer data, int[] datawidths, boolean[] isinstanced, int startindex) {
		if (datawidths.length != isinstanced.length)
			throw new IllegalArgumentException("datawidths and isinstanced arguments must be the same length.");

		int stride = 0;
		for (int width : datawidths) {
			if (width < 0 || width > 4)
				throw new IllegalArgumentException("Component width must be between 0-4 inclusive.");

			stride += width;
		}

		if (data.capacity() % stride != 0)
			throw new IllegalArgumentException("Total data width is not a multiple of the input data.");

		VertexBuffer strippedbuffer = new VertexBuffer(GLArrayType.Array);
		strippedbuffer.store(data);

		storeStriped(strippedbuffer, stride, datawidths, isinstanced, startindex, data.capacity());
	}
	public void storeStriped(VertexBuffer strippedbuffer, int stride, int[] datawidths, boolean[] isinstanced, int startindex, int totalsize) {
		for (int i=0, offset=0; i<datawidths.length; i++) {
			bindAttribute(
					strippedbuffer,
					startindex,
					datawidths[i],
					stride,
					offset,
					isinstanced[i]
			);
			offset += datawidths[i];
			startindex++;
		}

		numberofVertcies = totalsize / stride;
	}

	public void bindAttribute(VertexBuffer vbo, int index, int datawidth) {
		bindAttribute(vbo, index, datawidth, 0, 0, false);
	}

	/*
		datawidth: Specifies the number of components of this vertex attribute. Must be 1, 2, 3, 4
		stride: Specifies the offset between consecutive generic vertex attributes in components
		offset: Specifies a offset of this component in components
	 */
	public void bindAttribute(VertexBuffer vbo, int index, int datawidth, int stride, int offset, boolean instanced) {
		if (!bind())
			return;

		GL20.glEnableVertexAttribArray(index);
		int datatypeinbytes = vbo.getDataType().getSize();
		vbo.bindAttribute(index, false, datawidth, stride*datatypeinbytes, offset*datatypeinbytes);
		if (instanced) {
			setAttributeInstaced(index);

			int newinstances = (int)(vbo.getSizeInBytes() / vbo.getDataType().getSize() / 3);
			if (maxInstances == 0) {
				maxInstances = newinstances;
			} else {
				maxInstances = Math.min(maxInstances, newinstances);
			}
		}

		vbos[index] = vbo;

		Renderer.checkErrors();
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
		for (int i = 0; i< vbos.length; i++) {
			if (vbos[i] == null)
				return;

			GL20.glEnableVertexAttribArray(i);
		}
	}
	public void disableAllAttributes() {
		if (!bind())
			return;

		for (int i = 0; i< vbos.length; i++) {
			if (vbos[i] == null)
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
			GL11.glDrawElements(renderMode.getGLType(), numberOfIndices, DataType.UInteger.getGLEnum(), 0);
		else
			GL11.glDrawArrays(renderMode.getGLType(), 0, numberofVertcies);
		//TODO: Add call count for profiler
	}

	public void renderInstanced(int instances) {
		if (!bind())
			return;

		instances = Math.min(maxInstances, instances);

		if (isIndexed()) {
			//TODO: Support indexed buffers with GLDrawElementsInstanced
		} else {
			GL31.glDrawArraysInstanced(renderMode.getGLType(), 0, numberofVertcies, instances);
		}
	}

	public void unbind() {
		GL30.glBindVertexArray(0);
	}

	public final boolean isIndexed() { return numberOfIndices != 0; }

	public final int getNumberOfIndices() { return numberOfIndices; }
	public final int getNumberofVertcies() { return numberofVertcies; }

	// Doesn't delete VBOs as they may be being used. Will get custom GC do that.
	@Override
	public void dispose() {
		if (isDisposed())
			return;

		System.out.println("Deleting VAO ID" + ID);

		GL30.glDeleteVertexArrays(ID);
		VertexArrayManager.unregister(this);
		isDisposed = true;
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
