package gloop.graphics.rendering;

import gloop.resources.Disposable;
import gloop.general.exceptions.ObjectDisposedException;
import gloop.resources.ResourceManager;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.GL_PRIMITIVES_GENERATED;
import static org.lwjgl.opengl.GL33.GL_ANY_SAMPLES_PASSED;
import static org.lwjgl.opengl.GL33.GL_TIME_ELAPSED;

public class GPUQuery implements Disposable {
	public enum Type {
		SamplesPassed(GL_SAMPLES_PASSED),
		AnySamplesPassed(GL_ANY_SAMPLES_PASSED),
		PrimitivesGenerated(GL_PRIMITIVES_GENERATED),
		TimeElapsed(GL_TIME_ELAPSED);

		private final int GLEnum;
		Type(int glenum) {
			GLEnum = glenum;
		}
		public int getGLEnum() { return GLEnum; }

	}

	private final int ID = glGenQueries();
	private final Type type;
	private long startTime, endTime;
	private boolean isDisposed;

	private boolean inUse = false;

	{
		GPUQueryManager.register(this);
	}

	GPUQuery(Type type){
		this.type = type;
	}

	public void start(){
		if (isDisposed())
			throw new ObjectDisposedException("Cannot use disposed GPUQuery ("+ID+").");

		glBeginQuery(type.getGLEnum(), ID);

		inUse = true;
		startTime = System.currentTimeMillis();
	}

	public void end(){
		if (!isInUse())
			return;

		glEndQuery(type.getGLEnum());
	}

	public boolean isResultReady(){
		if (!isInUse())
			return false;

		return glGetQueryObjecti(ID, GL_QUERY_RESULT_AVAILABLE) == GL_TRUE;
	}

	public boolean isInUse(){
		return inUse;
	}

	public Type getType() { return type; }

	int getID() { return ID; }

	public int getResult(){
		inUse = false;
		endTime = System.currentTimeMillis();

		return glGetQueryObjecti(ID, GL_QUERY_RESULT);
	}

	public long getElapesedMilliseconds() {
		if (isInUse())
			return System.currentTimeMillis() - startTime;

		return endTime - startTime;
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
	public void dispose(){
		glDeleteQueries(ID);
		GPUQueryManager.unregister(this);
		isDisposed = true;
	}
}
