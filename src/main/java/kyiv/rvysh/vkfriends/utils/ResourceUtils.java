package kyiv.rvysh.vkfriends.utils;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class ResourceUtils {
	private ResourceUtils() {
	}

	public static String classpathResourceAsString(String path) {
		String result = "";
		ClassLoader classLoader = ResourceUtils.class.getClassLoader();
		try {
			result = IOUtils.toString(classLoader.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
