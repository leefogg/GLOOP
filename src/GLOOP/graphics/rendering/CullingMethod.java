package GLOOP.graphics.rendering;

import GLOOP.graphics.data.models.Model3D;

import java.util.List;

public interface CullingMethod {
	void calculateSceneOcclusion(List<Model3D> models);
}
