package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.models.Model;
import engine.graphics.models.Model3D;
import engine.graphics.models.ModelFactory;
import engine.graphics.particlesystem.ParticleSystem;
import engine.graphics.shading.materials.SingleColorMaterial;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;
import engine.physics.data.AABB;
import engine.math.Quaternion;
import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class ForwardRenderer extends Renderer {
	private boolean isDisposed;
	private FrameBuffer buffer;

	private static final RenderQueryPool QUERY_POOL = new RenderQueryPool(10);
	// Used to render bouding boxes
	private static Model3D CUBE;
	private static final AABB BOUNDING_BOX = new AABB(0,0,0,0,0,0);
	private static final Vector3f POSITION = new Vector3f();
	private static final Quaternion ROTATION = new Quaternion();

	static {
		try {
			CUBE = ModelFactory.getModel("res/models/primitives/cube.obj", new SingleColorMaterial(Color.red));
		} catch (IOException e) {
			e.printStackTrace();
			Viewport.close();
			System.exit(1);
		}
	}

	ForwardRenderer() {
		PixelFormat pixelformat = Settings.EnableHDR ? PixelFormat.RGB16F : PixelFormat.RGB16;
		buffer = new FrameBuffer(Viewport.getWidth(), Viewport.getHeight(), new PixelFormat[]{ pixelformat }, true, true);
	}

	@Override
	public void bind(Renderer previoustechnique) {
		if (isDisposed)
			return;

		buffer.bind();

		if (previoustechnique != null)
			if (previoustechnique instanceof ForwardRenderer)
				Renderer.clear(true, true, true);
			else
				previoustechnique.getBuffer().blitTo(buffer, true, true, false);
	}

	@Override
	protected void renderScene() {
		HashSet<Model3D> models = scene.getModels();

		// Rendere occuders
		for (Model3D model : models) {
			if (cannotRenderModel(model))
				continue;
			if (!model.isOccluder())
				continue;

			model.render();
		}

		List<RenderQuery> pendingqueries = QUERY_POOL.getPendingQueries();

		// Update models' visibility using previous frame(s) queries
		for (int i=0; i<pendingqueries.size(); i++) {
			RenderQuery renderquery = pendingqueries.get(i);
			if (renderquery.isResultAvailable())
				renderquery.Model.cansee = renderquery.isModelVisible();
			//TODO: Clear RenderQueries periodically so list no longer contains models removed from the scene
		}


		// Render new occusion queries
		Renderer.enableColorBufferWriting(false, false, false, false);
		Renderer.enableDepthBufferWriting(false);
		for (Model3D model : models) {
			if (cannotRenderModel(model))
				continue;
			if (model.isOccluder())
				continue;
			// If model outside frustum, dont bother with occlusion query
			// As render query has delay,
			// we can throw the result away if object is definately outside frustum this frame
			boolean failedfrustumtest = model.isOccuded();
			if (model.getNumberOfVertcies() < Settings.OcclusionQueryMinVertcies) {// Is not worth a render query?
				// Never going to perform occlusion query for this object so have to set it to result of frustum test
				model.cansee = !failedfrustumtest;
				continue;
			} else {
				// Going to be performing occlusion query so can only set to occluded if we're sure
				if (failedfrustumtest)
					model.cansee = false;
			}


			// Passed frustum test, do occlusion test if ready
			// Skip if query is still pending
			if (QUERY_POOL.isModelPending(model))
				continue;

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

		int ObjectsRendered = 0;
		for (Model3D model : models) {
			if (cannotRenderModel(model))
				continue;
			if (model.isOccluder())
				continue;
			if (model.cansee) {
				model.render();
				ObjectsRendered++;
			}
		}
		System.out.println(ObjectsRendered);

		if (!scene.getParticleSystems().isEmpty()) {
			Renderer.enableFaceCulling(false);
			for (ParticleSystem ps : scene.getParticleSystems())
				ps.render();
			Renderer.popFaceCullingEnabledState();
		}
	}

	private boolean cannotRenderModel(Model model) {
		return model.getMaterial().usesDeferredPipeline();
		//TODO: Add PBR material filter when added
	}

	@Override
	public Texture getTexture() {
		return buffer.getColorTexture(0);
	}

	@Override
	public FrameBuffer getBuffer() {
		return buffer;
	}

	@Override
	public boolean isDisposed() {
		return isDisposed;
	}

	@Override
	public void dispose() {
		if (isDisposed)
			return;

		buffer.dispose();

		isDisposed = true;
	}
}
