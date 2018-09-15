package engine.graphics.rendering;

import engine.graphics.models.Model;
import engine.graphics.models.Model3D;

import java.util.List;

public class FrustumCullingMethod implements CullingMethod {

	@Override
	public void calculateSceneOcclusion(List<Model3D> models) {
		for (Model3D model : models) {
			boolean outsidefrusum = model.isOccluded();
			model.setVisibility(outsidefrusum ? Model.Visibility.NotVisible : Model.Visibility.Unknown);
		}
	}
}
