package gloop.graphics.rendering.texturing;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.IntBuffer;
import java.nio.file.Paths;
import java.util.HashSet;

import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;

public class TextureManager {
	private static final HashSet<Texture> TEXTURES = new HashSet<>(); // List of all texturing in VRAM

	private static final int NUMBER_OF_TEXTURE_UNITS = GL11.glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS); // TODO: Check why environement map doesn't work when less then 16
	private static final TextureUnit[] TEXTURE_UNITS = new TextureUnit[NUMBER_OF_TEXTURE_UNITS];

	static {
		for (int i = 0; i< NUMBER_OF_TEXTURE_UNITS; i++)
			TEXTURE_UNITS[i] = new TextureUnit(i);
	}

	public static Texture newTexture(String path, PixelComponents externalformat, PixelFormat internalformat) throws IOException {
		File texturepath = Paths.get(path).toFile();
		if (!texturepath.exists())
			throw new FileNotFoundException("The file " + path + " was not found on disk.");

		return newTexture(path, ImageIO.read(texturepath), externalformat, internalformat);
	}
	public static Texture newTexture(String name, BufferedImage image, PixelComponents externalformat, PixelFormat internalformat) {
		System.out.print("Loading texture for file \"" + name + "\"... ");

		Texture existingTexture = getTexture(name);
		if (existingTexture != null) {
			System.out.println("Texture already loaded. Returning existing.");
			return existingTexture;
		}

		System.out.println("First load. Returning new.");
		return new Texture(name, image, externalformat, internalformat);
	}

	public static void bindAlbedoMap(Texture albedomap) {
		bindTextureToUnit(albedomap, TextureUnit.ALBEDO_MAP);
	}
	public static void bindNormalMap(Texture normalmap) { bindTextureToUnit(normalmap, TextureUnit.NORMAL_MAP); }
	public static void bindSpecularMap(Texture specularmap) {
		bindTextureToUnit(specularmap, TextureUnit.SPECULAR_MAP);
	}
	public static void bindDepthMap(Texture depthmap) {	bindTextureToUnit(depthmap, TextureUnit.DEPTH_MAP);	}
	public static void bindReflectionMap(CubeMap reflectionmap) { bindTextureToUnit(reflectionmap, TextureUnit.ENVIRONMENT_MAP);	}

	public static void bindTextureToUnit(Texture tex, int unit) {
		if (tex == null || tex.isDisposed())
			return;
		if (unit >= NUMBER_OF_TEXTURE_UNITS)
			return;

		TEXTURE_UNITS[unit].setTexture(tex);
	}

	private static Texture getTexture(CharSequence name) {
		for (Texture tex : TEXTURES)
			if (tex.getName().contentEquals(name))
				return tex;

		return null;
	}

	public int getNumberOfSupportedTextureUnits() {
		return NUMBER_OF_TEXTURE_UNITS;
	}

	static void register(Texture tex) {
		System.out.println("Texture \"" + tex.getName() + "\" loaded as ID " + tex.getID() + ".");
		TEXTURES.add(tex);
	}
	static void unregister(Texture tex) {
		System.out.println("Deleted texture " + tex.getName() + " (ID:" + tex.getID() + ").");
		TEXTURES.remove(tex);
	}

	public static void cleanup() {
		System.out.println("Deleting " + TEXTURES.size() + " texturing..");
		IntBuffer textureIDs = BufferUtils.createIntBuffer(TEXTURES.size());
		for (Texture texture : TEXTURES)
			textureIDs.put(texture.getID());

		textureIDs.flip();
		GL11.glDeleteTextures(textureIDs);

		TEXTURES.clear(); // Unregister all
	}
}
