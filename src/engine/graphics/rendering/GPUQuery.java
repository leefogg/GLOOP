package engine.graphics.rendering;

import engine.Disposable;
import engine.general.exceptions.ObjectDisposedException;
import engine.resources.ResourceManager;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.GL_PRIMITIVES_GENERATED;
import static org.lwjgl.opengl.GL33.GL_ANY_SAMPLES_PASSED;
import static org.lwjgl.opengl.GL33.GL_TIME_ELAPSED;

public class GPUQuery implements Disposable {
	public static enum Type {
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
	private final Type Type;
	private long StartTime, EndTime;
	private boolean isDisposed;

	private boolean inUse = false;

	{
		GPUQueryManager.register(this);
	}

	GPUQuery(Type type){
		this.Type = type;
	}

	public void start(){
		if (isDisposed())
			throw new ObjectDisposedException("Cannot use disposed GPUQuery ("+ID+").");

		glBeginQuery(Type.getGLEnum(), ID);

		inUse = true;
		StartTime = System.currentTimeMillis();
	}

	public void end(){
		if (!isInUse())
			return;

		glEndQuery(Type.getGLEnum());
	}

	public boolean isResultReady(){
		if (!isInUse())
			return false;

		return glGetQueryObjecti(ID, GL_QUERY_RESULT_AVAILABLE) == GL_TRUE;
	}

	public boolean isInUse(){
		return inUse;
	}

	public Type getType() { return Type; }

	int getID() { return ID; }

	public int getResult(){
		inUse = false;
		EndTime = System.currentTimeMillis();

		return glGetQueryObjecti(ID, GL_QUERY_RESULT);
	}

	public long GetElapesedMilliseconds() {
		if (isInUse())
			return System.currentTimeMillis() - StartTime;

		return EndTime - StartTime;
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
