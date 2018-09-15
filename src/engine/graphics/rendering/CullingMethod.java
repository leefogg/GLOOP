package engine.graphics.rendering;

import engine.graphics.models.Model3D;

import java.util.List;

public interface CullingMethod {
	void calculateSceneOcclusion(List<Model3D> models);
}
