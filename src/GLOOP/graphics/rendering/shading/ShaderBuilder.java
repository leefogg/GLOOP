package GLOOP.graphics.rendering.shading;

import GLOOP.general.exceptions.CurcularReferenceException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

public class ShaderBuilder {
	private StringBuilder sourceCode;

	public ShaderBuilder(String shaderfilepath) throws IOException, CurcularReferenceException {
		System.out.println("Shader being loaded \""+shaderfilepath+"\"");
		sourceCode = loadTextFile(shaderfilepath);
	}

	public ShaderBuilder addDefines(Iterable<Map.Entry<String, String>> defines) {
		for (Map.Entry<String, String> define : defines)
			define(define);

		return this;
	}
	public ShaderBuilder define(Map.Entry<String, String> kvPair) {
		int firstnewline = sourceCode.indexOf("\n") + 1;
		sourceCode.insert(firstnewline, "#define " + kvPair.getKey() + " " + kvPair.getValue() + "\n");

		return this;
	}

	public static StringBuilder loadTextFile(String path) throws IOException, CurcularReferenceException {
		return loadTextFile(path, new ArrayList<>());
	}
	private static StringBuilder loadTextFile(String path, ArrayList<String> importedfilepaths) throws IOException, CurcularReferenceException {
		File file = new File(path);
		if (!file.exists())
			throw new FileNotFoundException(path + " was not found on the filesystem.");
		if (importedfilepaths.contains(path))
			throw new CurcularReferenceException(path + " was already included");

		Path directory = Paths.get(file.getParent());

		StringBuilder filecontent = new StringBuilder(500);
		BufferedReader objfilereader = new BufferedReader(new FileReader(path));
		while(!objfilereader.ready()){}
		importedfilepaths.add(path);

		String line;
		while ((line = objfilereader.readLine()) != null)  {
			line = line.trim();

			if (line.startsWith("#include ")) {
				String importedfilename = between(line, '<', '>');
				String importedfilepath = directory.resolve(importedfilename).toString();

				StringBuilder importedfilecontent = loadTextFile(importedfilepath, importedfilepaths);
				filecontent.append(importedfilecontent);
			} else {
				filecontent.append(line + "\n");
			}
		}
		objfilereader.close();

		return filecontent;
	}

	private static String between(String text, char start, char end) {
		int startindex = text.indexOf(start);
		if (startindex == -1)
			return text;

		text = text.substring(startindex+1);
		int endindex = text.indexOf(end);

		return text.substring(0, endindex);
	}

	public String getSourceCode() {
		return sourceCode.toString();
	}
}
