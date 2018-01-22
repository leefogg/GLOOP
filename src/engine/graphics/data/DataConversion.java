package engine.graphics.data;

import org.lwjgl.BufferUtils;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public final class DataConversion {

	public static FloatBuffer toGLBuffer(Vector2f vector) {
		FloatBuffer array = BufferUtils.createFloatBuffer(2);
		vector.load(array);
		array.flip();

		return array;
	}

	public static FloatBuffer toGLBuffer(Vector3f vector) {
		FloatBuffer array = BufferUtils.createFloatBuffer(3);
		vector.load(array);
		array.flip();

		return array;
	}

	public static FloatBuffer toGLBuffer(float[] data) {
		FloatBuffer array = BufferUtils.createFloatBuffer(data.length);
		array.put(data);
		array.flip();

		return array;
	}

	public static FloatBuffer toGLBuffer(Vector3f[] data) {
		FloatBuffer array = BufferUtils.createFloatBuffer(data.length*3);
		for (Vector3f v : data)
			v.load(array);

		array.flip();

		return array;
	}

	public static FloatBuffer toGLBuffer(Vector2f[] data) {
		FloatBuffer array = BufferUtils.createFloatBuffer(data.length*2);
		for (Vector2f v : data)
			v.load(array);

		array.flip();

		return array;
	}

	public static IntBuffer toGLBuffer(int[] data) {
		IntBuffer array = BufferUtils.createIntBuffer(data.length);
		array.put(data);
		array.flip();

		return array;
	}

	public static ByteBuffer toGLBuffer(byte[] data) {
		ByteBuffer array = BufferUtils.createByteBuffer(data.length);
		array.put(data);
		array.flip();

		return array;
	}

	public static float[] toFloatArray(Vector3f[] vertices) {
		float[] data = new float[vertices.length*3];

		int i = 0;
		for (Vector3f vert : vertices) {
			data[i++] = vert.x;
			data[i++] = vert.y;
			data[i++] = vert.z;
		}

		return data;
	}

	public static float[] toFloatArray(Vector2f[] vertices) {
		float[] data = new float[vertices.length*2];

		int i= 0;
		for (Vector2f vert : vertices) {
			data[i++] = vert.x;
			data[i++] = vert.y;
		}

		return data;
	}

	public static int nextPowerOf2(int x) {
		x--;
		x |= x >> 1;  // handle  2 bit numbers
		x |= x >> 2;  // handle  4 bit numbers
		x |= x >> 4;  // handle  8 bit numbers
		x |= x >> 8;  // handle 16 bit numbers
		x |= x >> 16; // handle 32 bit numbers
		x++;

		return x;
	}
}
