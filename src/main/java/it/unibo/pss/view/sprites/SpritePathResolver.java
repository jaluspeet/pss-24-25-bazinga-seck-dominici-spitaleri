package it.unibo.pss.view.sprites;

public class SpritePathResolver {
	private static String prefix = "/sprites/isometric";

	public static String getPrefix() {
		return prefix;
	}

	public static void setMode(String mode) {
		if ("isometric".equalsIgnoreCase(mode)) {
			prefix = "/sprites/isometric";
		} else if ("topdown".equalsIgnoreCase(mode)) {
			prefix = "/sprites/topdown";
		}
	}
}
