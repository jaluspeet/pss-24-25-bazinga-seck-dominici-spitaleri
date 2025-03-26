package it.unibo.bazinga.view.sprites;

/**
 * Used to resolve the path of the sprites based on the mode (isometric or topdown).
 */
public class SpritePathResolver {
	private static String prefix = "/sprites/isometric";

	/**
	 * Returns the prefix of the path of the sprites.
	 * 
	 * @return the prefix of the path of the sprites
	 */
	public static String getPrefix() {
		return prefix;
	}

	/**
	 * Sets the mode of the sprites.
	 * 
	 * @param mode the mode of the sprites
	 */
	public static void setMode(String mode) {
		if ("isometric".equalsIgnoreCase(mode)) {
			prefix = "/sprites/isometric";
		} else if ("topdown".equalsIgnoreCase(mode)) {
			prefix = "/sprites/topdown";
		}
	}
}
