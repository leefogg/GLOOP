package engine.graphics.shading;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class ShaderBuilder {
	private StringBuilder sourceCode;

	public ShaderBuilder(String shaderfilepath) throws IOException {
		System.out.println("Shader being loaded \""+shaderfilepath+"\"");
		sourceCode = loadTextFile(shaderfilepath);
	}

	public ShaderBuilder addDefines(String[] defines) {
		for (String define : defines)
			define(define);

		return this;
	}
	public ShaderBuilder define(String constant) {
		int firstnewline = sourceCode.indexOf("\n") + 1;
		sourceCode.insert(firstnewline, "#define " + constant + "\n");

		return this;
	}

	private StringBuilder loadTextFile(String filepath) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(filepath));
		StringBuilder sourcecode = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			//TODO: Reccursive loading
			sourcecode.append(line);
			sourcecode.append('\n');
		}
		reader.close();

		return sourcecode;
	}

	public String getSourceCode() {
		return sourceCode.toString();
	}
}
