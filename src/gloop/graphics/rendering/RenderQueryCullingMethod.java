package gloop.graphics.rendering;

import gloop.general.exceptions.UnsupportedException;
import gloop.graphics.data.models.Model;
import gloop.graphics.data.models.Model3D;
import gloop.graphics.data.models.ModelFactory;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.shading.materials.SingleColorMaterial;
import gloop.graphics.rendering.texturing.FrameBuffer;
import gloop.graphics.rendering.texturing.PixelFormat;
import gloop.general.math.Quaternion;
import gloop.physics.data.AABB;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class RenderQueryCullingMethod implements CullingMethod {
	private static final FrameBuffer OCCLUSION_BUFFER = new FrameBuffer(Viewport.getWidth()/2, Viewport.getHeight()/2, PixelFormat.R8);
	private static final RenderQueryPool QUERY_POOL = new RenderQueryPool(10);
	// Used to render bouding boxes
	private static final AABB BOUNDING_BOX = new AABB(0,0,0,0,0,0);
	private static final Vector3f POSITION = new Vector3f();
	private static final Quaternion ROTATION = new Quaternion();
	private static Model3D Cube;
	private static SingleColorMaterial RenderMaterial;

	private int minObjVertcies = 500;

	public RenderQueryCullingMethod() throws IOException, UnsupportedException {
		RenderMaterial = new SingleColorMaterial(Color.red);
		Cube = ModelFactory.getModel("res/_SYSTEM/models/Cube.obj", RenderMaterial);
	}

	public void setMinimumRequiredVertcies(int numvertcies) { minObjVertcies = numvertcies; }
	public int getMinimumRequiredVertcies() { return minObjVertcies; }


	@Override
	public void calculateSceneOcclusion(List<Model3D> models) {
		boolean previouscamera = Renderer.UseDebugCamera;
		Renderer.useDebugCamera(false);

		OCCLUSION_BUFFER.bind();
		Renderer.clear(true, true, false);

		// Render occuders
		for (Model3D model : models) {
			if (!model.isOccluder())
				continue;
			boolean failedfrustumtest = model.isOccluded();
			if (failedfrustumtest) {
				model.setVisibility(Model.Visibility.NotVisible);
				continue;
			}
			model.setVisibility(Model.Visibility.Visible);

			Material modelsmaterial = model.getMaterial();
			model.setMaterial(RenderMaterial);
			model.render();
			model.setMaterial(modelsmaterial);
		}

		// Update models' visibility using previous frame(s) queries
		List<RenderQuery> pendingqueries = QUERY_POOL.getPendingQueries();
		for (RenderQuery renderquery : pendingqueries) {
			if (renderquery.isResultAvailable())
				renderquery.model.setVisibility(renderquery.isModelVisible() ? Model.Visibility.Visible : Model.Visibility.NotVisible);
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

			if (model.getNumberOfVertcies() < minObjVertcies || !model.hasBoundingBox()) { // Not reccomended by user or possible to do query
				// Never going to perform occlusion query for this object so have to set it to result of frustum test
				continue;
			}

			model.getBoundingBox(BOUNDING_BOX);
			model.getPostition(POSITION);
			model.getRotation(ROTATION);
			Cube.setScale(BOUNDING_BOX.width, BOUNDING_BOX.height, BOUNDING_BOX.depth);
			Cube.setPosition(POSITION.x, POSITION.y, POSITION.z);
			Cube.setRotation(ROTATION);
			RenderQuery query = QUERY_POOL.startQuery(model);
			Cube.render();
			query.end();
		}
		Renderer.popDepthBufferWritingState();
		Renderer.popColorBufferWritingState();
		Renderer.useDebugCamera(previouscamera);
	}
}
