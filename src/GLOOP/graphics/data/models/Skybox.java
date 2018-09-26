package GLOOP.graphics.data.models;

import GLOOP.graphics.rendering.DepthFunction;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.shading.materials.CubeMapMaterial;
import GLOOP.graphics.rendering.texturing.CubeMap;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class Skybox extends Model3D {
	private static final VertexArray vertexGeometry;
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
			vertexGeometry = VertexArrayManager.getVAO("SkyboxVertcies", cubeverts, false, false, false);
	}

	public Skybox(CubeMap texture) throws IOException {
		super(vertexGeometry, new CubeMapMaterial(texture));
	}

	@Override
	public void getModelMatrix(Matrix4f out) {
		out.load(modelMatrix);
	}

	@Override
	public void render() {
		Renderer.setDepthFunction(DepthFunction.LessOrEqual);
		super.render();
		Renderer.setDepthFunction(DepthFunction.Less);
	}
}
