package engine.graphics.rendering;

import engine.graphics.Settings;
import engine.graphics.models.Model;
import engine.graphics.models.Model3D;
import engine.graphics.particlesystem.ParticleSystem;
import engine.graphics.textures.FrameBuffer;
import engine.graphics.textures.PixelFormat;
import engine.graphics.textures.Texture;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;

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
		// Rendere occuders
		for (Model model : scene.getModels()) {
			if (cannotRenderModel(model))
				continue;
			if (!(model instanceof Model3D && ((Model3D)model).isOccuder()))
				continue;

			model.render();
		}

		// Render last frame's objects if they passed test
		for (Model model : scene.getModels()) {
			GPUQuery queryresult = RenderQueries.get(model);
			if (queryresult == null)
				continue;

			if (queryresult.isResultReady())
				RenderQueries.remove(model);
			else
				continue;
			//TODO: Clear RenderQueries periodically so list no longer contains models removed from the scene

			model.cansee = queryresult.getResult() == GL11.GL_TRUE;
		}

		int ObjectsRendered = 0;
		for (Model model : scene.getModels()) {
			if (cannotRenderModel(model))
				continue;

			if (model.cansee) {
				model.render();
				ObjectsRendered++;
			}
		}
		System.out.println(ObjectsRendered);

		// Render new occusion queries
		Renderer.enableColorBufferWriting(false, false, false, false);
		Renderer.enableDepthBufferWriting(false);
		for (Model model : scene.getModels()) {
			if (cannotRenderModel(model))
				continue;

			if (model instanceof Model3D && ((Model3D)model).isOccuder())
				continue;

			GPUQuery query = new GPUQuery(GPUQuery.Type.AnySamplesPassed);
			query.start();
			model.render(); // TODO: Render object's bounding box
			query.end();
			RenderQueries.put(model, query);
		}
		Renderer.popDepthBufferWritingState();
		Renderer.popColorBufferWritingState();

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
