package engine.graphics.models;

import engine.animation.Transform2D;
import engine.graphics.rendering.Viewport;
import engine.graphics.shading.materials.FullBrightMaterial;
import engine.graphics.shading.materials.GUIShader;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Model2D extends Model {
	private static final VertexArray quadGeometry = getQuadGeometry();
	private static GUIShader shader;

	static {
		try {
			shader = new GUIShader();
		} catch (Exception e) {
			e.printStackTrace();
			Viewport.close();
			System.exit(1);
		}
	}

	public static VertexArray getQuadGeometry() {
		if (quadGeometry == null) {
			//TODO: Create via MeshBuilder
			float[] strippedVAOdata = new float[] {
					0,-1,0,	0,0, // Top left
					1,-1,0,	1,0, // Top right
					0,0,0,	0,1, // Bottom left
					1,0,0,	1,1  // Bottom right
			};
			VertexArray quadgeometry = VertexArrayManager.getVAO("Quad", strippedVAOdata, true, false, false);
			quadgeometry.renderMode = RenderMode.TriangleStrip;
			return quadgeometry;
		}

		return quadGeometry;
	}

	private final Vector2f
		position = new Vector2f(), // Position in pixels
		size = new Vector2f(); // Size in pixels
	protected Transform2D transform = new Transform2D();
	//TODO: top-left alignment or centre (use enum)
	public Model2D(int x, int y, int width, int height) {
		super(quadGeometry, new FullBrightMaterial(shader));

		setPosition(x, y);
		setScale(width, height);
	}

	//TODO: Add alignment options (top-left or centre)

	public void getPostition(Vector2f position) { transform.getPosition(position); }

	public void setPostition(Vector3f position) {
		setPosition((int)position.x, (int)position.y);
	}
	public void setPosition(int x, int y) {
		int
		screenwidth = Viewport.getWidth(),
		screenheight = Viewport.getHeight();

		transform.setPosition(
					x / (float)screenwidth * 2 - 1,
					1f - y / (float)screenheight * 2
				);
	}

	public void getScale(Vector2f scale) {
		transform.getScale(scale);
	}

	public void setScale(Vector3f scale) {
		setScale(scale.x, scale.y);
	}
	public void setScale(float width, float height) {
		int
		screenwidth = Viewport.getWidth(),
		screenheight = Viewport.getHeight();

		transform.setScale(
				1f / (screenwidth / (width*2)),
				1f / (screenheight / (height*2))
		);
	}

	//TODO: Flip and flop functions
	public void flipVertically() {
		position.y += size.y;
		size.y = -size.y;
	}

	public Model2D Clone() {
		// Always stores ints in transform so casting should do nothing
		Model2D gui = new Model2D((int)position.x, (int)position.y, (int)size.x, (int)size.y);
		gui.setMaterial(material);

		return gui;
	}
	@Override
	public void getModelMatrix(Matrix4f out) { transform.getModelMatrix(out); }

	//TODO: ToModel3D method
}
