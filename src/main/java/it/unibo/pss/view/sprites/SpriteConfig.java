package it.unibo.pss.view.sprites;

/**
 * Holds the sprite configuration for an entity type.
 */
public class SpriteConfig {
	private final String subFolder;
	private final String prefix;

	public SpriteConfig(String subFolder, String prefix) {
		this.subFolder = subFolder;
		this.prefix = prefix;
	}

	public String getSubFolder() {
		return subFolder;
	}

	public String getPrefix() {
		return prefix;
	}
}

