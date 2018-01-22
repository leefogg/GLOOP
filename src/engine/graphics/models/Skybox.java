package engine.graphics.models;

import engine.graphics.rendering.DepthFunction;
import engine.graphics.rendering.Renderer;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.materials.CubeMapMaterial;
import engine.graphics.shading.materials.CubeMapShader;
import engine.graphics.textures.CubeMap;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class Skybox extends Model {
	private static final VertexArray vertexGeometry;
	private static CubeMapShader shader;
	private static final Matrix4f modelMatrix = new Matrix4f();
	static {
		float[] cubeverts = new float[] {
				-1,  1, -1,
			    -1, -1, -1,
			     1, -1, -1,
			     1, -1, -1,
			     1,  1, -1,
			    -1,  1, -1,

			    -1, -1,  1,
			    -1, -1, -1,
			    -1,  1, -1,
			    -1,  1, -1,
			    -1,  1,  1,
			    -1, -1,  1,

			     1, -1, -1,
			     1, -1,  1,
			     1,  1,  1,
			     1,  1,  1,
			     1,  1, -1,
			     1, -1, -1,

			    -1, -1,  1,
			    -1,  1,  1,
			     1,  1,  1,
			     1,  1,  1,
			     1, -1,  1,
			    -1, -1,  1,

			    -1,  1, -1,
			     1,  1, -1,
			     1,  1,  1,
			     1,  1,  1,
			    -1,  1,  1,
			    -1,  1, -1,

			    -1, -1, -1,
			    -1, -1,  1,
			     1, -1, -1,
			     1, -1, -1,
			    -1, -1,  1,
			     1, -1,  1
			};
			vertexGeometry = VertexArrayManager.newVAO("SkyboxVertcies", cubeverts, false, false, false);
	}

	public Skybox(CubeMap texture) throws IOException {
		super(vertexGeometry, new CubeMapMaterial(texture));
	}

	@Override
	public Matrix4f getModelMatrix() {
		return modelMatrix;
	}

	@Override
	public void render() {
		Renderer.setDepthFunction(DepthFunction.LessOrEqual);
		super.render();
		Renderer.setDepthFunction(DepthFunction.Less);
	}
}
