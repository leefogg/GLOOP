package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.models.Model;
import engine.graphics.models.Model3D;
import engine.graphics.particlesystem.ParticleSystem;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;

import java.util.HashSet;
import java.util.List;

public class ForwardRenderer extends Renderer {
	private boolean isDisposed;
	private FrameBuffer buffer;

	private static final RenderQueryPool QUERY_POOL = new RenderQueryPool(10);

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
		for (Model model : models) {
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
		for (Model model : models) {
			if (cannotRenderModel(model))
				continue;
			if (model.isOccluder())
				continue;
			// If model outside frustum, dont bother with render Query
			if (model.isOccuded()) {
				// As render Query has delay,
				// we can throw the result away as object is definately outside frustum this frame
				model.cansee = false;
				continue;
			}

			// Passed frustum test, do occlusion test if ready
			// Skip if query is still pending
			if (QUERY_POOL.isModelPending(model))
				continue;

			RenderQuery query = QUERY_POOL.startQuery(model);
			model.render(); // TODO: Render object's bounding box
			query.end();
		}
		Renderer.popDepthBufferWritingState();
		Renderer.popColorBufferWritingState();

		int ObjectsRendered = 0;
		for (Model model : models) {
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
