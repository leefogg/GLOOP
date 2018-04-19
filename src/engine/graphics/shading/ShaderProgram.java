package engine.graphics.shading;

import engine.Disposable;
import engine.graphics.cameras.Camera;
import engine.graphics.rendering.Renderer;
import engine.graphics.shading.GLSL.Uniform16f;
import engine.resources.ResourceManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public abstract class ShaderProgram implements Disposable {
	private boolean isDisposed = false;

	private final int programID;
	private final VertexShader vertexShader;
	private final FragmentShader fragmentShader;

	private Matrix4f vpmatrix = new Matrix4f(); // TODO: Make static

	// Uniform locations
	private Uniform16f
		modelMatrix,
		VPMatrix,
		inverseVPMatrix;

	//TODO: vertex only and vertex, geo and fragment constructors
	public ShaderProgram(String vertexshaderpath, String fragmentshaderpath) throws ShaderCompilationException, IOException {
		this(
			new VertexShader(new ShaderBuilder(vertexshaderpath).getSourceCode()),
			new FragmentShader(new ShaderBuilder(fragmentshaderpath).getSourceCode())
		);
	}
	public ShaderProgram(String vertexshaderpath, String fragmentshaderpath, String[] defines) throws ShaderCompilationException, IOException {
		this(
			new VertexShader(new ShaderBuilder(vertexshaderpath).addDefines(defines).getSourceCode()),
			new FragmentShader(new ShaderBuilder(fragmentshaderpath).addDefines(defines).getSourceCode())
		);
	}
	public ShaderProgram(VertexShader vertexshader, FragmentShader fragmentshader) throws ShaderCompilationException {
		programID = glCreateProgram();
		System.out.println("Created Shader Program ID " + programID);
		glAttachShader(programID, vertexshader.getID());
		glAttachShader(programID, fragmentshader.getID());
		glLinkProgram(programID);
		glValidateProgram(programID);
		if (glGetProgrami(programID, GL_LINK_STATUS) == GL11.GL_FALSE)
			throw new ShaderCompilationException(glGetProgramInfoLog(programID, 512));
		if (glGetProgrami(programID, GL_VALIDATE_STATUS) == GL11.GL_FALSE)
			throw new ShaderCompilationException(glGetProgramInfoLog(programID, 512));

		bindFragmentOutputLocations();
		bindAttributes();
		getAllUniformLocations();

		Renderer.checkErrors();

		this.vertexShader = vertexshader;
		this.fragmentShader = fragmentshader;

		ShaderManager.register(this);

	}

	protected abstract void bindAttributes();

	public void bind() {
		if (isDisposed)
			return;

		ShaderManager.setCurrentShader(this);
	}
	public static void useNone() {
		glUseProgram(0);
	} // TODO: Delete

	private void getAllUniformLocations() {
		getMandatoryUniformLocations();
		getCustomUniformLocations();
		//setDefaultCustomUniformValues(); // TODO: GLGetErrors doesn't like this?
	}

	public void setCameraUniforms(Camera camera, Matrix4f modelmatrix) {
		Matrix4f projectionmatrix = camera.getProjectionMatrix();
		Matrix4f viewmatrix = camera.getViewMatrix();
		Matrix4f.mul(projectionmatrix, viewmatrix, vpmatrix);
		setViewProjectionMatrix(vpmatrix);
		vpmatrix.invert();
		setInverseViewProjectionMatrix(vpmatrix);
		setModelMatrix(modelmatrix);
	}
	private void getMandatoryUniformLocations() {
		modelMatrix      = new Uniform16f(this, "ModelMatrix");
		VPMatrix         = new Uniform16f(this, "VPMatrix");
		inverseVPMatrix  = new Uniform16f(this, "InverseVPMatrix");
	}
	protected abstract void getCustomUniformLocations();
	protected abstract void setDefaultCustomUniformValues();
	protected void bindFragmentOutputLocations() {}

	protected void bindAttribute(String attributename, int attributeindex) {
		int location = glGetAttribLocation(programID, attributename);
		if (location == -1) {
			System.err.println("Attribute \"" + attributename + "\" not found!");
		} else {
			glEnableVertexAttribArray(location);
			glBindAttribLocation(programID, attributeindex, attributename);
		}
	}

	protected void bindFragmentOutput(String name, int fraglocation) { //TODO: Test
		int location = GL30.glGetFragDataLocation(programID, name);
		if (location == -1)
			System.err.println("Fragment binding \"" + name + "\" not found!");
		else
			GL30.glBindFragDataLocation(programID, fraglocation, name);
	}

	public int getID() { return programID; }

	public void setModelMatrix(Matrix4f modelmatrix) { modelMatrix.set(modelmatrix); }
	public void setViewProjectionMatrix(Matrix4f vpmatrix) { VPMatrix.set(vpmatrix); }
	public void setInverseViewProjectionMatrix(Matrix4f inversevpmatrix) { inverseVPMatrix.set(inversevpmatrix); }

	public abstract boolean supportsTransparency();

	@Override
	public void dispose() {
		// TODO: Wait until the next frame as shader may be running
		deleteProgram();
		useNone();

		ShaderManager.unregister(this);
		isDisposed = true;
	}

	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	void deleteProgram() {
		// TODO Research. Is this correct procedure?
		glDetachShader(programID, vertexShader.getID());
		glDetachShader(programID, fragmentShader.getID());
		vertexShader.dispose();
		fragmentShader.dispose();
		glDeleteProgram(programID);

		isDisposed = true;
	}

	@Override
	public void requestDisposal() {
		ResourceManager.queueDisposal(this);
	}
}