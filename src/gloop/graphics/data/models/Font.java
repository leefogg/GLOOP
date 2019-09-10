package gloop.graphics.data.models;

import gloop.general.exceptions.FormatException;
import gloop.graphics.rendering.BlendFunction;
import gloop.graphics.rendering.Renderer;
import gloop.graphics.rendering.shading.materials.FontMaterial;
import gloop.graphics.rendering.shading.materials.Material;
import gloop.graphics.rendering.texturing.Texture;
import gloop.graphics.rendering.texturing.TextureFilter;
import org.lwjgl.util.vector.ReadableVector3f;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Font {
	private static class TextureAtlasCharacterInfo {
		public int x, y, width, height, xoffset, yoffset, xadvance;

		public TextureAtlasCharacterInfo(int x, int y, int width, int height, int xoffset, int yoffset, int xadvance) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.xoffset = xoffset;
			this.yoffset = yoffset;
			this.xadvance = xadvance;
		}
	}

	private static final Vector3f DEFAULT_FONT_COLOR = new Vector3f(1,1,1);
	private static final Model2D QUAD = new Model2D(0,0,0,0);
	private static Texture FontAtlas;
	private float fontSize, lineHeight;

	private final Map<Integer, TextureAtlasCharacterInfo> characterInformation = new HashMap();

	public Font(Texture textureatlas, String fontfilepath) throws IOException, FormatException {
		setMaterialSingleton();

		FontAtlas = textureatlas;
		FontAtlas.setFilteringMode(TextureFilter.Linear);

		File fontfile = Paths.get(fontfilepath).toFile();
		if (!fontfile.exists())
			throw new FileNotFoundException("The file " + fontfilepath + " was not found on disk.");

		parseFontFile(fontfilepath);
	}

	private void setMaterialSingleton() throws IOException {
		Material quadmaterial = QUAD.getMaterial();
		if (quadmaterial instanceof FontMaterial)
			return;

		QUAD.setMaterial(new FontMaterial());
	}

	private void parseFontFile(String fontfilepath) throws IOException, FormatException {
		String spliregex = "(\\s+|=)";
		FormatException invalidfontfileexcepton = new FormatException("Unable to parse font file");

		BufferedReader fontfile = new BufferedReader(new FileReader(fontfilepath));
		String line;
		while ((line = fontfile.readLine()) != null) {
			if (line.startsWith("char ")) {
				line = line.substring("char ".length());
				String[] parts = line.split(spliregex);

				try {
					String idstring = findKeyValue("id", parts);
					String xstring = findKeyValue("x", parts);
					String ystring = findKeyValue("y", parts);
					String widthstring = findKeyValue("width", parts);
					String heightstring = findKeyValue("height", parts);
					String xoffsetstring = findKeyValue("xoffset", parts);
					String yoffsetstring = findKeyValue("yoffset", parts);
					String xadvancestring = findKeyValue("xadvance", parts);
					if (idstring == null || xstring == null || ystring == null || widthstring == null || heightstring == null || xoffsetstring == null || yoffsetstring == null || xadvancestring == null)
						throw invalidfontfileexcepton;

					characterInformation.put(
							Integer.parseInt(idstring),
							new TextureAtlasCharacterInfo(
									Integer.parseInt(xstring),
									Integer.parseInt(ystring),
									Integer.parseInt(widthstring),
									Integer.parseInt(heightstring),
									Integer.parseInt(xoffsetstring),
									Integer.parseInt(yoffsetstring),
									Integer.parseInt(xadvancestring)
							)
					);
				} catch (NumberFormatException ex) {
					throw invalidfontfileexcepton;
				}
			} else if (line.startsWith("info ")) {
				line = line.substring("info ".length());
				String[] parts = line.split(spliregex);
				 try {
				 	fontSize = Float.parseFloat(findKeyValue("size", parts));
				 } catch (NumberFormatException ex) {
				 	throw invalidfontfileexcepton;
				 }
			} else if (line.startsWith("commmon ")) {
				line = line.substring("common ".length());
				String[] parts = line.split(spliregex);
				try {
					lineHeight = Float.parseFloat(findKeyValue("lineHeight", parts));
				} catch (NumberFormatException ex) {
					throw invalidfontfileexcepton;
				}
			}
		}
	}

	private String findKeyValue(String key, String[] keyvaluepair) {
		for(int i=0; i<keyvaluepair.length; i+=2)
			if (keyvaluepair[i].matches(key))
				return keyvaluepair[i+1];

		return null;
	}


	public void render(char[] string, int x, int y, float size) {
		render(string, x, y, size, DEFAULT_FONT_COLOR);
	}
	public void render(char[] string, int x, int y, float size, Vector3f color) {
		render(string, x, y, size, color, 0.2f);
	}
	public void render(char[] string, int x, int y, float size, Vector3f color, float thickness) {
		render(string, x, y, size, color, thickness, 0.05f - 0.05f * (size / 1000f));
	}
	public void render(char[] string, int x, int y, float size, ReadableVector3f color, float thickness, float edgewidth) {
		FontMaterial material = (FontMaterial) QUAD.getMaterial();
		material.setFontTextureAtlas(FontAtlas);
		material.setThickness(thickness);
		material.setEdgeWidth(edgewidth);
		material.setColor(color);

		float scaler = size / fontSize;

		Renderer.enableBlending(true);
		Renderer.setBlendFunctionsState(BlendFunction.One, BlendFunction.One);
		Renderer.enableStencilTesting(false);
		Renderer.enableDepthTesting(false);
		Renderer.enableDepthBufferWriting(false);

		for (char character : string) {
			TextureAtlasCharacterInfo characterinfo = characterInformation.get((int)character);

			QUAD.setPosition((int)(x+characterinfo.xoffset * scaler), (int)(y+characterinfo.yoffset * scaler));
			QUAD.setScale(characterinfo.width * scaler, characterinfo.height * scaler);
			material.setOffset(characterinfo.x, characterinfo.y);
			material.setScale(characterinfo.width, characterinfo.height);

			QUAD.render();

			x += characterinfo.xadvance * scaler;
		}

		Renderer.popDepthBufferWritingState();
		Renderer.popDepthTestingEnabledState();
		Renderer.popStencilTestingState();
		Renderer.popBlendFunctionsState();
		Renderer.popBlendingEnabledState();
	}
}
