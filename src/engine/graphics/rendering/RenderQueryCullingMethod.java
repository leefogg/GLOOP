package engine.graphics.rendering;

import engine.general.exceptions.UnsupportedException;
import engine.graphics.Settings;
import engine.graphics.models.Model;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.shading.materials.Material;
import engine.graphics.shading.materials.SingleColorMaterial;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.math.Quaternion;
import engine.physics.data.AABB;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class RenderQueryCullingMethod implements CullingMethod {
	private static final FrameBuffer OCCLUSION_BUFFER = new FrameBuffer(Viewport.getWidth()/2, Viewport.getHeight()/2, PixelFormat.R8);
	private static final RenderQueryPool QUERY_POOL = new RenderQueryPool(10);
	// Used to render bouding boxes
	private static Model3D CUBE;
	private static final AABB BOUNDING_BOX = new AABB(0,0,0,0,0,0);
	private static final Vector3f POSITION = new Vector3f();
	private static final Quaternion ROTATION = new Quaternion();
	private static SingleColorMaterial RenderMaterial;

	private int MinObjVertcies = 500;

	public RenderQueryCullingMethod() throws IOException, UnsupportedException {
		RenderMaterial = new SingleColorMaterial(Color.red);
		CUBE = ModelFactory.getModel("res/_SYSTEM/models/Cube.obj", RenderMaterial);
	}

	public void setMinimumRequiredVertcies(int numvertcies) { MinObjVertcies = numvertcies; }
	public int getMinimumRequiredVertcies() { return MinObjVertcies; }


	@Override
	public void calculateSceneOcclusion(List<Model3D> models) {
		boolean previouscamera = Renderer.useDebugCamera;
		Renderer.useDebugCamera(false);

		OCCLUSION_BUFFER.bind();
		Renderer.clear(true, true, false);

		// Render occuders
		for (Model3D model : models) {
			if (!model.isOccluder())
				continue;

			Material modelsmaterial = model.getMaterial();
			model.setMaterial(RenderMaterial);
			model.render();
			model.setMaterial(modelsmaterial);
		}

		// Update models' visibility using previous frame(s) queries
		List<RenderQuery> pendingqueries = QUERY_POOL.getPendingQueries();
		for (RenderQuery renderquery : pendingqueries) {
			if (renderquery.isResultAvailable())
				renderquery.Model.setVisibility(renderquery.isModelVisible() ? Model.Visibility.Visible : Model.Visibility.NotVisible);
		}

		// Render new occusion queries
		Renderer.enableColorBufferWriting(false, false, false, false);
		Renderer.enableDepthBufferWriting(false);
		// Always render occusion queries though game camera
		for (Model3D model : models) {
			if (model.isOccluder())
				continue;

			// If model outside frustum, dont bother with occlusion query
			boolean failedfrustumtest = model.isOccluded();
			if (failedfrustumtest)
				model.setVisibility(Model.Visibility.NotVisible);


			// Create query if object might be visible, even if known not visible this frame
			// Need to keep queries running
			if (failedfrustumtest)
				continue;

			if (model.getNumberOfVertcies() < MinObjVertcies || !model.hasBoundingBox()) { // Not reccomended by user or possible to do query
				// Never going to perform occlusion query for this object so have to set it to result of frustum test
				continue;
			}

			model.getBoundingBox(BOUNDING_BOX);
			model.getPostition(POSITION);
			model.getRotation(ROTATION);
			CUBE.setScale(BOUNDING_BOX.width, BOUNDING_BOX.height, BOUNDING_BOX.depth);
			CUBE.setPosition(POSITION.x, POSITION.y, POSITION.z);
			CUBE.setRotation(ROTATION);
			RenderQuery query = QUERY_POOL.startQuery(model);
			CUBE.render();
			query.end();
		}
		Renderer.popDepthBufferWritingState();
		Renderer.popColorBufferWritingState();
		Renderer.useDebugCamera(previouscamera);
	}
}
