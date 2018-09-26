package GLOOP.graphics.rendering;

import GLOOP.graphics.data.models.Model;
import GLOOP.graphics.data.models.Model3D;

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
