package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.models.Model;
import engine.graphics.particlesystem.ParticleSystem;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.HashSet;

public class ForwardRenderer extends Renderer {
	private boolean isDisposed;
	private FrameBuffer buffer;
	private HashMap<Model, GPUQuery> RenderQueries = new HashMap<>();

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
		HashSet<Model> models = scene.getModels();

		// Rendere occuders
		for (Model model : models) {
			if (cannotRenderModel(model))
				continue;
			if (!model.isOccluder())
				continue;

			model.render();
		}

		// Update models' visibility using previous frame(s) queries
		for (Model model : models) {
			if (model.isOccluder())
				continue;

			GPUQuery queryresult = RenderQueries.get(model);
			if (queryresult != null && queryresult.isResultReady()) {
				RenderQueries.remove(model);
				model.cansee = queryresult.getResult() == GL11.GL_TRUE;
			}
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
			// If model outside frustum, dont both with render query
			if (model.isOccuded()) {
				// As render query has delay,
				// we can throw the result away as object is definately outside frustum this frame
				model.cansee = false;
				continue;
			}

			// Passed frustum test, do occlusion test if ready
			GPUQuery queryresult = RenderQueries.get(model);
			if (queryresult != null) // Query already running
				continue;

			GPUQuery query = RenderQueryPool.getQuery(GPUQuery.Type.AnySamplesPassed);
			query.start();
			model.render(); // TODO: Render object's bounding box
			query.end();
			RenderQueries.put(model, query);
		}
		Renderer.popDepthBufferWritingState();
		Renderer.popColorBufferWritingState();

		int ObjectsRendered = 0;
		for (Model model : models) {
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
