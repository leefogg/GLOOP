package engine.graphics.fonts;

import engine.general.exceptions.FormatException;
import engine.graphics.models.Model2D;
import engine.graphics.rendering.BlendFunction;
import engine.graphics.rendering.Renderer;
import engine.graphics.shading.materials.FontMaterial;
import engine.graphics.shading.materials.Material;
import engine.graphics.textures.Texture;
import engine.graphics.textures.TextureFilter;
import org.lwjgl.util.vector.Vector3f;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;

public class Font {
	private class TextureAtlasCharacterInfo {
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

	private static Vector3f DefaultFontColor = new Vector3f(1,1,1);
	private static Model2D Quad = new Model2D(0,0,0,0);
	private static Texture FontAtlas;
	private float FontSize, LineHeight;

	private HashMap<Integer, TextureAtlasCharacterInfo> CharacterInformation = new HashMap();

	public Font(Texture textureatlas, String fontfilepath) throws IOException, FormatException {
		SetMaterialSingleton();

		FontAtlas = textureatlas;
		FontAtlas.setFilteringMode(TextureFilter.Linear);

		File fontfile = Paths.get(fontfilepath).toFile();
		if (!fontfile.exists())
			throw new FileNotFoundException();

		parseFontFile(fontfilepath);
	}

	private void SetMaterialSingleton() throws IOException {
		Material quadmaterial = Quad.getMaterial();
		if (quadmaterial instanceof FontMaterial)
			return;

		Quad.setMaterial(new FontMaterial());
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
					String idstring = FindKeyValue("id", parts);
					String xstring = FindKeyValue("x", parts);
					String ystring = FindKeyValue("y", parts);
					String widthstring = FindKeyValue("width", parts);
					String heightstring = FindKeyValue("height", parts);
					String xoffsetstring = FindKeyValue("xoffset", parts);
					String yoffsetstring = FindKeyValue("yoffset", parts);
					String xadvancestring = FindKeyValue("xadvance", parts);
					if (idstring == null || xstring == null || ystring == null || widthstring == null || heightstring == null || xoffsetstring == null || yoffsetstring == null || xadvancestring == null)
						throw invalidfontfileexcepton;

					CharacterInformation.put(
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
				 	FontSize = Float.parseFloat(FindKeyValue("size", parts));
				 } catch (NumberFormatException ex) {
				 	throw invalidfontfileexcepton;
				 }
			} else if (line.startsWith("commmon ")) {
				line = line.substring("common ".length());
				String[] parts = line.split(spliregex);
				try {
					LineHeight = Float.parseFloat(FindKeyValue("lineHeight", parts));
				} catch (NumberFormatException ex) {
					throw invalidfontfileexcepton;
				}
			}
		}
	}

	private String FindKeyValue(String key, String[] keyvaluepair) {
		for(int i=0; i<keyvaluepair.length; i+=2)
			if (keyvaluepair[i].matches(key))
				return keyvaluepair[i+1];

		return null;
	}


	public void render(char[] string, int x, int y, float size) {
		render(string, x, y, size, DefaultFontColor);
	}
	public void render(char[] string, int x, int y, float size, Vector3f color) {
		render(string, x, y, size, color, 0.2f);
	}
	public void render(char[] string, int x, int y, float size, Vector3f color, float thickness) {
		render(string, x, y, size, color, thickness, 0.05f - 0.05f * (size / 1000f));
	}
	public void render(char[] string, int x, int y, float size, Vector3f color, float thickness, float edgewidth) {
		FontMaterial material = (FontMaterial)Quad.getMaterial();
		material.setFontTextureAtlas(FontAtlas);
		material.setThickness(thickness);
		material.setEdgeWidth(edgewidth);
		material.setColor(color);

		float scaler = size / FontSize;

		Renderer.enableBlending(true);
		Renderer.setBlendFunctionsState(BlendFunction.One, BlendFunction.One);
		Renderer.enableStencilTesting(false);
		Renderer.enableDepthTesting(false);
		Renderer.enableDepthBufferWriting(false);

		for (char character : string) {
			TextureAtlasCharacterInfo characterinfo = CharacterInformation.get((int)character);

			Quad.setPosition((int)(x+characterinfo.xoffset * scaler), (int)(y+characterinfo.yoffset * scaler));
			Quad.setScale(characterinfo.width * scaler, characterinfo.height * scaler);
			material.setOffset(characterinfo.x, characterinfo.y);
			material.setScale(characterinfo.width, characterinfo.height);

			Quad.render();

			x += characterinfo.xadvance * scaler;
		}

		Renderer.popDepthBufferWritingState();
		Renderer.popDepthTestingEnabledState();
		Renderer.popStencilTestingState();
		Renderer.popBlendFunctionsState();
		Renderer.popBlendingEnabledState();
	}
}
