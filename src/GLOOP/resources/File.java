package GLOOP.resources;

public class File {
	public static String getFileExtension(String filepath) {
		int dotindex = filepath.lastIndexOf('.');
		if (dotindex == -1 || dotindex == filepath.length())
			return "";

		return filepath.substring(dotindex+1);
	}
}
