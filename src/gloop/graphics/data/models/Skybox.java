package gloop.graphics.data.models;

import gloop.graphics.rendering.DepthFunction;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.shading.materials.CubeMapMaterial;
import gloop.graphics.rendering.texturing.CubeMap;
import org.lwjgl.util.vector.Matrix4f;

import java.io.IOException;

public class Skybox extends Model3D {
	private static final VertexArray VERTEX_GEOMETRY;
	private static final Matrix4f MODEL_MATRIX = new Matrix4f();
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
			VERTEX_GEOMETRY = VertexArrayManager.getVAO("SkyboxVertcies", cubeverts, false, false, false);
	}

	public Skybox(CubeMap texture) throws IOException {
		super(VERTEX_GEOMETRY, new CubeMapMaterial(texture));
	}

	@Override
	public void getModelMatrix(Matrix4f out) {
		out.load(MODEL_MATRIX);
	}

	@Override
	public void render() {
		Renderer.setDepthFunction(DepthFunction.LessOrEqual);
		super.render();
		Renderer.setDepthFunction(DepthFunction.Less);
	}
}
