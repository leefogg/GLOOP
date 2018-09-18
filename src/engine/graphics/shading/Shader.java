package engine.graphics.shading;

import engine.general.Disposable;
import engine.resources.ResourceManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

abstract class Shader implements Disposable {
	enum Type {
		Vertex  (GL_VERTEX_SHADER),
		Fragment(GL_FRAGMENT_SHADER),
		Geometry(GL_GEOMETRY_SHADER);

		int GLType;
		Type(int GLType) {this.GLType = GLType;}

		public int getGLType() {
			return GLType;
		}
	}

	private final int ID;
	private boolean disposed;

	protected Shader(String sourcecode, Type type) throws ShaderCompilationException {
		ID = glCreateShader(type.getGLType());
		glShaderSource(ID, sourcecode);
		glCompileShader(ID);
		if (glGetShaderi(ID, GL_COMPILE_STATUS) == GL11.GL_FALSE)
			throw new ShaderCompilationException(glGetShaderInfoLog(ID, 512));
	}

	public int getID() {return ID;}

	@Override
	public void requestDisposal() {
		ResourceManager.queueDisposal(this);
	}

	@Override
	public boolean isDisposed() {
		return disposed;
	}

	@Override
	public void dispose() {
		glDeleteShader(ID);
		disposed = true;
	}
}
