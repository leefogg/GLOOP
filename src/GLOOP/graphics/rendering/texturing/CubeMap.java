package GLOOP.graphics.rendering.texturing;

import GLOOP.graphics.data.DataType;
import GLOOP.graphics.rendering.Renderer;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

public class CubeMap extends Texture {
	public static final Vector3f DefaultPosition = new Vector3f(0,0,0);
	public static final Vector3f DefaultSize = new Vector3f(100000,100000,100000);

	private Vector3f position = new Vector3f();
	private Vector3f size = new Vector3f();

	public CubeMap(String name, int resolution, PixelFormat internalformat, Vector3f position, Vector3f size) {
		super( // Setup and bind first side
				name,
				null,
				TextureTarget.CubeMapRight,
				PixelComponents.RGB,
				internalformat,
				TextureType.Cubemap,
				DataType.UByte,
				resolution,
				resolution
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

		type = TextureType.Cubemap;

		Renderer.checkErrors();

		setSize(size);
		setPosition(position);
	}

	public CubeMap(String name, String[] imagespaths, PixelComponents externalformat, PixelFormat internalformat) throws IOException {
		this(name, loadFaces(imagespaths), externalformat, internalformat, DefaultPosition, DefaultSize);
	}
	public CubeMap(String name, String[] imagespaths, PixelComponents externalformat, PixelFormat internalformat, Vector3f position, Vector3f size) throws IOException {
		this(name, loadFaces(imagespaths), externalformat, internalformat, position, size);
	}
	//TODO: Duplicate constructor, only difference is getPixelData in base constructor call
	public CubeMap(String name, BufferedImage[] images, PixelComponents externalformat, PixelFormat internalformat, Vector3f position, Vector3f size) {
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

		setSize(size);
		setPosition(position);
	}

	private static final BufferedImage[] loadFaces(String[] paths) throws IOException {
		BufferedImage[] textures = new BufferedImage[6];
		for (int i = 0; i < 6; i++)
			textures[i] = ImageIO.read(Paths.get(paths[i]).toFile());

		return textures;
	}

	public void setSize(Vector3f size) { this.size.set(size); }
	public void getSize(Vector3f out) { out.set(size); }

	public void setPosition(Vector3f position) { setPosition(position.x, position.y, position.z); }
	public void setPosition(float x, float y, float z) { position.set(x, y, z); }
	public void getPosition(Vector3f out){ out.set(position); }
}
