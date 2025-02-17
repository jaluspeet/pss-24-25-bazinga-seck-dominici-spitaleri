package it.unibo.pss.view.sprites;

import it.unibo.pss.model.world.World;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Random;

/** Loads and caches world tile sprites from a given path.
    Land textures are cached, while water textures are rotated sequentially.
    For water tiles, the texture index is computed by shifting the global cycle
    and adding an offset based on the tile's coordinates. */
public class WorldSpriteLoader {
	private final List<Image> landTextures;
	private final List<Image> waterTextures;
	private final Map<World.Tile, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();
	
	private static final long WATER_ANIMATION_PERIOD = 500;

	public WorldSpriteLoader(String basePath) {
		landTextures = loadTextures(basePath, "land");
		waterTextures = loadTextures(basePath, "water");
	}

	private List<Image> loadTextures(String basePath, String prefix) {
		List<Image> textures = new ArrayList<>();
		int index = 0;
		while (true) {
			String imagePath = basePath + "/" + prefix + "_" + index + ".png";
			try {
				Image img = new Image(getClass().getResourceAsStream(imagePath));
				if (img.getWidth() == 0) break;
				textures.add(img);
				index++;
			} catch (Exception e) {
				break;
			}
		}
		return textures;
	}

	public Image getTileTexture(World.Tile tile) {
		if (tile.getType() == World.Tile.TileType.LAND) {
			if (cache.containsKey(tile)) {
				return cache.get(tile);
			}
			Image texture = getRandomTexture(landTextures);
			cache.put(tile, texture);
			return texture;
		} else {
			if (waterTextures.isEmpty()) return null;
			int globalIndex = (int) ((System.currentTimeMillis() / WATER_ANIMATION_PERIOD) % waterTextures.size());
			int offset = (tile.getX() + tile.getY()) % waterTextures.size();
			int waterIndex = (globalIndex + offset) % waterTextures.size();
			return waterTextures.get(waterIndex);
		}
	}

	private Image getRandomTexture(List<Image> textures) {
		if (textures.isEmpty()) return null;
		return textures.get(random.nextInt(textures.size()));
	}
}
