package it.unibo.pss.view.sprites;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;
import javafx.scene.image.Image;
import java.util.*;

/**
 * This class is responsible for loading and caching the sprites used to represent the world tiles.
 */
public class WorldSpriteLoader {
	private List<Image> landSprites;
	private List<Image> waterSprites;
	private final Map<World.Tile, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();
	private final String relativeBasePath;

	/**
	 * Creates a new instance of the WorldSpriteLoader class.
	 * @param relativeBasePath The relative path to the directory containing the sprite images.
	 */
	public WorldSpriteLoader(String relativeBasePath) {
		this.relativeBasePath = relativeBasePath;
		reload();
	}

	/**
	 * Reloads the sprite images from the file system.
	 */
	public void reload() {
		String basePath = SpritePathResolver.getPrefix() + relativeBasePath;
		landSprites = loadSprites(basePath, "land");
		waterSprites = loadSprites(basePath, "water");
		cache.clear();
	}

	/**
	 * Gets the sprite image for the specified tile.
	 * @param tile The tile for which to get the sprite image.
	 * @param now The current time in nanoseconds.
	 * @return The sprite image for the specified tile.
	 */
	private List<Image> loadSprites(String basePath, String prefix) {
		List<Image> sprites = new ArrayList<>();
		for (int index = 0; ; index++) {
			String imagePath = basePath + "/" + prefix + "_" + index + ".png";
			try {
				Image img = new Image(getClass().getResourceAsStream(imagePath));
				if (img.isError()) break;
				sprites.add(img);
			} catch (Exception e) {
				break;
			}
		}
		return sprites;
	}

	/**
	 * Gets the sprite image for the specified tile.
	 * @param tile The tile for which to get the sprite image.
	 * @param now The current time in nanoseconds.
	 * @return The sprite image for the specified tile.
	 */
	public Image getTileSprite(World.Tile tile, long now) {
		if (tile.getType() == World.Tile.TileType.LAND) {
			return cache.computeIfAbsent(tile, _ -> getRandomSprite(landSprites));
		}
		if (waterSprites.isEmpty()) return null;
		long currentMillis = now / 1_000_000;
		int globalIndex = (int) ((currentMillis / (SharedConstants.CAMERA_FRAMERATE * 10)) % waterSprites.size());
		return waterSprites.get((globalIndex + (tile.getX() + tile.getY())) % waterSprites.size());
	}

	/**
	 * Gets a random sprite image from the specified list.
	 * @param sprites The list of sprite images from which to get a random one.
	 * @return A random sprite image from the specified list.
	 */
	private Image getRandomSprite(List<Image> sprites) {
		return sprites.isEmpty() ? null : sprites.get(random.nextInt(sprites.size()));
	}
}
