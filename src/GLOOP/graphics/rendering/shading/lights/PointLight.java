package GLOOP.graphics.rendering.shading.lights;

import GLOOP.graphics.cameras.Camera;
import GLOOP.graphics.rendering.ForwardRenderer;
import GLOOP.graphics.rendering.Renderer;
import GLOOP.graphics.rendering.texturing.*;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

import static org.lwjgl.opengl.GL30.GL_COLOR_ATTACHMENT0;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;

public final class PointLight extends Light {
	private static int CubeMapsCreatedProbe;

	private class ShadowProbe extends EnvironmentProbe {
		public ShadowProbe(int resolution, int framesUntilRenew, float zfar) {
			super(
				new CubeMap(
					"DepthMapCProbe" + CubeMapsCreatedProbe++,
					resolution,
					PixelFormat.R8,
					position,
					new Vector3f(100,100,100) // This doesn't matter
				),
				framesUntilRenew
			);

			RENDERCAM.setzfar(zfar);
		}

		@Override
		public void renew() {
			if (isDisposed())
				return;
			if (!isExpired())
				return;

			FrameBuffer previousframebuffer = FrameBuffer.getCurrent();

			ForwardRenderer renderer = Renderer.getForwardRenderer();
			Camera backupcam = Renderer.getCurrentCamera();
			renderer.getScene().setGameCamera(RENDERCAM);

			RENDERCAM.setDimensions(faceSizePixels, faceSizePixels);
			environmentMap.getPosition(Temp);
			RENDERCAM.setPosition(Temp);
			environmentMap.bind();
			frameBuffer.bind();


			for (int i=0; i<6; i++) {
				glFramebufferTexture2D(
						GL_FRAMEBUFFER,
						GL_COLOR_ATTACHMENT0,
						GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
						environmentMap.getID(),
						0
				);

				switchToFace(i);

				renderer.reset();
				renderer.renderShadowScene();
			}

			framesUntilRenew = renewDelayFrames;

			renderer.getScene().setGameCamera(backupcam);

			previousframebuffer.bind();
		}

		public float getzFar() { return RENDERCAM.getzfar(); }
	}

	private final Vector3f position = new Vector3f();
	private final Vector3f color = new Vector3f(1,1,1);
	private ShadowProbe probe;
	public float quadraticAttenuation = Integer.MAX_VALUE;

	public final Vector3f getPosition(Vector3f destination) {
		return destination.set(position);
	}

	public final void setPosition(Vector3f position) {
		setPosition(position.x, position.y, position.z);
	}
	public final void setPosition(float x, float y, float z) {
		position.set(x, y, z);

		if (isShadowMapEnabled())
			getShadowMap().setPosition(x,y,z);
	}

	public final Vector3f getColor(Vector3f dest) {
		dest.set(color);
		return dest;
	}

	public final void setColor(Vector3f color) { setColor(color.x, color.y, color.z); }
	public final void setColor(float r, float g, float b) {
		color.set(r,g,b);
		color.normalise();
	}

	@Override
	public boolean isComplex() {
		return isShadowMapEnabled();
	}

	@Override
	public boolean isShadowMapEnabled() {
		return probe != null;
	}

	@Override
	public void enableShadows(int resolution, int refreshRate, float zfar) {
		probe = new ShadowProbe(resolution, refreshRate, zfar);
	}

	@Override
	public void disableShadows() {
		probe.dispose();
		probe = null;
	}


	@Override
	public void updateShadowMap() {
		if (!isShadowMapEnabled())
			return;

		probe.renew();
	}

	@Override
	public CubeMap getShadowMap() {
		return probe.getEnvironmentMap();
	}
	public float getzFar() { return probe.getzFar(); }
}
