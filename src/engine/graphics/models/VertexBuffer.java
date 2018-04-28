package engine.graphics.models;

import engine.graphics.data.Buffer;
import engine.graphics.data.DataConversion;
import engine.graphics.rendering.Renderer;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class VertexBuffer extends Buffer {
	private static long
		TotalBytes,
		BytesAdded,
		BytesUpdated;

	private final int ID = GL15.glGenBuffers();
	private DataType Type;
	private final GLArrayType Arraytype;
	private DataVolatility Volatility;

	// TODO: Constructor to create striped VBO using variadic T[]

	public VertexBuffer(GLArrayType arraytype) {
		this(arraytype, 0, DataVolatility.Static, DataType.Float);
	}
	public VertexBuffer(GLArrayType arraytype, long size, DataVolatility volatility, DataType type) {
		super(size);
		Volatility = volatility;
		Arraytype = arraytype;
		Type = type;
		createEmptyBuffer(size);

		VertexBufferManager.register(this);
		Renderer.checkErrors();
	}

	public void store(FloatBuffer buffer) {
		store(buffer, DataType.Float);
	}
	public void store(FloatBuffer buffer, DataType datatype) {
		Type = datatype;

		bindBuffer(buffer);
	}

	public int getID() {
		return ID;
	}

	public DataType getDataType() {
		return Type;
	}

	public final void setVolatility(DataVolatility volatility) {
		Volatility = volatility;
	}


	public void store(IntBuffer buffer) {
		store(buffer, DataType.UInteger);
	}
	public void store(IntBuffer buffer, DataType datatype) {
		Type = datatype;

		bindBuffer(buffer);
	}

	private void bindBuffer(IntBuffer buffer) {
		if (!bind())
			return;

		GL15.glBufferData(
				Arraytype.getGLEnum(),
				buffer,
				Volatility.getGLEnum()
			);

		alloc(buffer.capacity() * DataType.Integer.getSize());
	}
	private void bindBuffer(FloatBuffer buffer) {
		if (!bind())
			return;

		GL15.glBufferData(
				Arraytype.getGLEnum(),
				buffer,
				Volatility.getGLEnum()
			);

		alloc(buffer.capacity() * DataType.Float.getSize());
	}
	private void createEmptyBuffer(long size) {
		if (!bind())
			return;

		GL15.glBufferData(
				Arraytype.getGLEnum(),
				size,
				Volatility.getGLEnum()
			);

		alloc(size);
	}

	public void update(float[] data, int startindex) {
		update(data, 0, startindex, data.length);
	}
	public void update(float[] data, int startelement, int startindex, int length) {
		if (startindex + length > Size)
			throw new IllegalArgumentException("Provided data is too long. " + (startindex + data.length) + " bytes is required while buffer is only " + Size + " bytes big.");
		if (startindex < 0)
			throw new IllegalArgumentException("Start index may not be less than 0");
		if (!bind())
			return;

		GL15.glBufferSubData(Arraytype.getGLEnum(), startelement*Type.getSize(), DataConversion.toGLBuffer(data, startindex, length));
	}

	void bindAttribute(int index) {
		bindAttribute(index, false, 4, 0, 0);
	}

	/*
		datawidth: Specifies the number of components per generic vertex attribute. Must be 1, 2, 3, 4
		stride: Specifies the byte offset between consecutive generic vertex attributes
		offset: Specifies a offset of this component in bytes
	 */
	void bindAttribute(int index, boolean isnormalised, int datawidth, int stride, int offset) {
		if (!bind())
			return;

		GL20.glVertexAttribPointer(
				index,
				datawidth,
				Type.getGLEnum(),
				isnormalised,
				stride,
				offset
			);
	}

	public boolean bind() {
		if (isDisposed())
			return false;

		GL15.glBindBuffer(Arraytype.getGLEnum(), ID);
		return true;
	}

	public void unbind() {
		GL15.glBindBuffer(Arraytype.getGLEnum(), 0);
	}

	@Override
	public void dispose() {
		System.out.println("Deleting VBO ID " + ID);

		GL15.glDeleteBuffers(ID);
		VertexBufferManager.unregister(this);

		super.dispose();

		TotalBytes -= Size;
	}

	@Override
	protected void alloc(long size) {
		this.Size = size;
		TotalBytes += size;
		BytesAdded += size;
	}

	@Override
	protected void update(long size) {
		BytesUpdated += size;
	}

	public static void clearStatistics() {
		BytesUpdated = 0;
		BytesAdded = 0;
	}

	public static final long getTotalBytes() {
		return TotalBytes;
	}
	public static final long getBytesAdded() {
		return BytesAdded;
	}
	public static final long getBytesUpdated() {
		return BytesUpdated;
	}
}
