package GLOOP.graphics.rendering.texturing;

import org.lwjgl.util.vector.Vector3f;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ColorTexture extends Texture {

	//TOOD: Lots more overrides
	public ColorTexture(String name, float a, float r, float g, float b) {
		this(name, new java.awt.Color(a,r,g,b));
	}
	public ColorTexture(String name, java.awt.Color color) {
		super(name, createColorImage(color), PixelComponents.RGBA, PixelFormat.RGBA8);

		setBorderColor(new Vector3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f));
		setFilteringMode(Filter.Nearest);
	}
	private static final BufferedImage createColorImage(java.awt.Color color) {
		BufferedImage image = new BufferedImage(16,16,BufferedImage.TYPE_INT_ARGB);
		Graphics2D canvas = image.createGraphics();
		canvas.setColor(color);
		canvas.fillRect(0,0,image.getWidth(), image.getHeight());
		canvas.dispose();

		return image;
	}

}
