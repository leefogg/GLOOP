package GLOOP.graphics.rendering.texturing;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class TextureUtilities {
	public static BufferedImage flipVertical(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(x,height-1-y,source.getRGB(x,y));
		return output;
	}

	public static BufferedImage flipHorizonal(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(width-1-x, y, source.getRGB(x, y));
		return output;
	}

	public static BufferedImage flipBoth(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(width-1-x, height-1-y, source.getRGB(x, y));
		return output;
	}

	public static BufferedImage stripRed(BufferedImage source) {
		return strip(source, 0xFFFF0000);
	}

	public static BufferedImage stripGreen(BufferedImage source) {
		return strip(source, 0xFF00FF00);
	}

	public static BufferedImage stripBlue(BufferedImage source) {
		return strip(source, 0xFF0000FF);
	}

	public static BufferedImage strip(BufferedImage source, int filter) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(x, y, source.getRGB(x, y) & filter);
		return output;
	}

	public static BufferedImage rotateClockwise(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(height, width, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(height-1-y, x, source.getRGB(x, y));
		return output;
	}

	public static BufferedImage rotateCounterClockwise(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(height, width, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(y, width-1-x, source.getRGB(x, y));
		return output;
	}

	public static BufferedImage greyscale(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(x, y, source.getRGB(x, y));
		return output;
	}

	public static BufferedImage invert(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++) {
				int
				pixel = source.getRGB(x, y),
				red = pixel >> 0 & 0xFF,
				green = pixel >> 8 & 0xFF,
				blue = pixel >> 16 & 0xFF;
				red = 255-red;
				green=255-green;
				blue= 255-blue;
				output.setRGB(x, y, red | green << 8 | blue << 16);
			}
		return output;
	}

	public static BufferedImage resize(BufferedImage source, int width, int height) {
		BufferedImage output = new BufferedImage(width, height, source.getType());
		Graphics canvas = output.getGraphics();
		canvas.drawImage(source, 0, 0, width, height, null);
		return output;
	}

	public static BufferedImage convertToAlpha(BufferedImage source) {
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++) {
				int
				pixel = source.getRGB(x, y),
				red = pixel >> 0 & 0xFF,
				green = pixel >> 8 & 0xFF,
				blue = pixel >> 16 & 0xFF,
				alpha = (red + green + blue) / 3;
				output.setRGB(x, y, red | green << 8 | blue << 16 | alpha << 24);
			}
		return output;
	}

	public static BufferedImage limitBits(BufferedImage source, int bits) {
		bits = Math.min(255, bits);
		int
		width = source.getWidth(),
		height = source.getHeight();
		BufferedImage output = new BufferedImage(width, height, source.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++) {
				int
				pixel = source.getRGB(x, y),
				red = pixel >> 0 & 0xFF,
				green = pixel >> 8 & 0xFF,
				blue = pixel >> 16 & 0xFF;
				red = red - red%(255/bits);
				green = green - green%(255/bits);
				blue = blue - blue%(255/bits);
				output.setRGB(x, y, red | green << 8 | blue << 16);
			}
		return output;
	}

	public static BufferedImage and(BufferedImage one, BufferedImage two) {
		int
		width = Math.min(one.getWidth(), two.getWidth()),
		height = Math.min(one.getHeight(),two.getHeight());
		BufferedImage output = new BufferedImage(width, height, one.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(x, y, one.getRGB(x, y) & two.getRGB(x, y));
		return output;
	}

	public static BufferedImage or(BufferedImage one, BufferedImage two) {
		int
		width = Math.min(one.getWidth(), two.getWidth()),
		height = Math.min(one.getHeight(),two.getHeight());
		BufferedImage output = new BufferedImage(width, height, one.getType());
		for (int y=0; y<height; y++)
			for (int x=0; x<width; x++)
				output.setRGB(x, y, one.getRGB(x, y) | two.getRGB(x, y));
		return output;
	}

	//TODO: AddAlpha to BufferedImage using another

	public static void save(BufferedImage image, String path) throws IOException {
		int dotindex = path.lastIndexOf('.');
		String extension = path.substring(dotindex+1);
		ImageIO.write(image, extension, new java.io.File(path));
	}
}
