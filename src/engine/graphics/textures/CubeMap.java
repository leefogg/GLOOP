package engine.graphics.textures;

import engine.graphics.models.DataType;
import engine.graphics.rendering.Renderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class CubeMap extends Texture {
	public CubeMap(String name, int size, PixelFormat internalformat) throws IOException {
		super( // Setup and bind first side
				name,
				null,
				TextureTarget.CubeMapRight,
				PixelComponents.RGB,
				internalformat,
				TextureType.Cubemap,
				DataType.UByte,
				size,
				size
		);
		// bind rest of sides
		writeData(TextureTarget.CubeMapLeft, internalformat, PixelComponents.RGB, dataType, null);
		writeData(TextureTarget.CubeMapTop, internalformat, PixelComponents.RGB, dataType, null);
		writeData(TextureTarget.CubeMapBottom, internalformat, PixelComponents.RGB, dataType, null);
		writeData(TextureTarget.CubeMapBack, internalformat, PixelComponents.RGB, dataType, null);
		writeData(TextureTarget.CubeMapFront, internalformat, PixelComponents.RGB, dataType, null);
		Renderer.checkErrors();

		setWrapMode(TextureWrapMode.EdgeClamp);
		setFilteringMode(Filter.Nearest);
	}
	public CubeMap(String name, String[] imagespaths, PixelComponents externalformat, PixelFormat internalformat) throws IOException {
		this(name, loadFaces(imagespaths), externalformat, internalformat);
	}
	public CubeMap(String name, BufferedImage[] images, PixelComponents externalformat, PixelFormat internalformat) {
		// TODO: Check images is 6 elements long
		// TODO: Check faces are 1:1 ratio
		super( // Setup and bind first side
				name,
				getPixelData(images[0], externalformat),
				TextureTarget.CubeMapRight,
				externalformat,
				internalformat,
				TextureType.Cubemap,
				DataType.UByte,
				images[0].getWidth(),
				images[0].getHeight()
		);
		// bind rest of sides
		writeData(TextureTarget.CubeMapLeft, internalformat, externalformat, dataType, getPixelData(images[1], externalformat));
		writeData(TextureTarget.CubeMapTop, internalformat, externalformat, dataType, getPixelData(images[2], externalformat));
		writeData(TextureTarget.CubeMapBottom, internalformat, externalformat, dataType, getPixelData(images[3], externalformat));
		writeData(TextureTarget.CubeMapBack, internalformat, externalformat, dataType, getPixelData(images[4], externalformat));
		writeData(TextureTarget.CubeMapFront, internalformat, externalformat, dataType, getPixelData(images[5], externalformat));

		setWrapMode(TextureWrapMode.EdgeClamp);
		setFilteringMode(Filter.Nearest);

		type = TextureType.Cubemap;

		Renderer.checkErrors();
	}

	private static final BufferedImage[] loadFaces(String[] paths) throws IOException {
		BufferedImage[] textures = new BufferedImage[6];
		for (int i = 0; i < 6; i++)
			textures[i] = ImageIO.read(Paths.get(paths[i]).toFile());

		return textures;
	}
}
