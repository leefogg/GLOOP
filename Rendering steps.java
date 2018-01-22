List<Model> visibleModels = new List<Model>(); // Dont use array because it's not resizable
do {
	sortObjects(visibleModels, SortingMethod.ClosestFirst);
	scene.render(visibleModels);
	TakeInput();
	scene.SimulateModels();
	scene.AnimateModels();
	
	visibleModels = scene.getVisibleObjects();
} while (running);

class Scene {
	private List<Model> models = ...;
	private final FrameBuffer scratchBuffer = ... // Maybe not needed?
	
	getVisibleObjects(Camera cam) {
		visibleModels.clear();
		
		scratchBuffer.bind();
		Renderer.disableColorWrites();
		int i=0;
		for(Model model : models)
			if (model.isVisible(cam))
				visibleModels.add(models.get(i));
		Renderer.enableColorWrites();	

		return visibleModels;
	}
}

class model {
	private CullingMethod cullingMethod = CullingMethod.None;
	
	public boolean isVisible(Camera cam) {
		Model highestLODmesh = meshes.get(meshes.length-1);
		VisibilityCertenty visibility = cam.frustm.cull(highestLODmesh.aabb);
		
		if (visibility == VisibilityCertenty.Visible)
			return true;
		
		return cullingMethod.query(highestLODmesh);
	}
	
	public void addLOD(Mesh mesh, int LOD) {
		meshes.add(mesh, LOD);	
		
		chooseAppropriateCullingMethod();
	}
	
	private void chooseAppropriateCullingMethod() {
		Model highestLODmesh = meshes.get(meshes.length-1);
		if (cullingMethod instanceof RenderQueryCullingMethod)
			return;
		
		int toofewverts = 10000;
		int maxverts = highestLODmesh.getNumberOfVertcies();
		if (maxverts <= toofewverts) {
			cullingMethod = CullingMethod.None;
			return;
		}
		
		cullingMethod = new RenderQueryCullingMethod();
	}
}

abstract class CullingMethod {
	public static final CullingMethod None = new VoidCullingMethod();

	abstract boolean isVisible(Model model);
}

private class VoidCullingMethod extends CullingMethod { // Package private
	VoidCullingMethod() { } // Can only be constructed by this package
	
	public boolean isVisible(Model model) {return true;}
}

public class RenderQueryCullingMethod extends CullingMethod {
	private final RenderQueryHandle handle = // Create render query handle;
	private boolean isVisible = true; // Visible unless proven otherwise
	
	public boolean isVisible(Model model) {
		if (handle.isPending())
			return true;
		
		boolean isvisible = handle.result();
		
		handle.render(model);
		
		return isvisible;
	}
}

enum VisibilityCertenty {
	Visible,
	Obscured,
	Culled,
	Uncertain;
}