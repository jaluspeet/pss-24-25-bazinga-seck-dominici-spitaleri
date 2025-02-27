package it.unibo.pss.view.sprites;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Random;

// load and cache world sprites from a given path
public class WorldSpriteLoader {
	private final List<Image> landSprites;
	private final List<Image> waterSprites;
	private final Map<World.Tile, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();
	
	// constructor for WorldSpriteLoader
	public WorldSpriteLoader(String basePath) {
		landSprites = loadSprites(basePath, "land");
		waterSprites = loadSprites(basePath, "water");
	}

	// load sprites from a given path
	private List<Image> loadSprites(String basePath, String prefix) {
		List<Image> sprites = new ArrayList<>();
		int index = 0;
		while (true) {
			String imagePath = basePath + "/" + prefix + "_" + index + ".png";
			try {
				Image img = new Image(getClass().getResourceAsStream(imagePath));
				if (img.getWidth() == 0) break;
				sprites.add(img);
				index++;
			} catch (Exception e) {
				break;
			}
		}
		return sprites;
	}

	// get sprite for a given tile
	public Image getTileSprite(World.Tile tile) {
		if (tile.getType() == World.Tile.TileType.LAND) {
			if (cache.containsKey(tile)) {
				return cache.get(tile);
			}
			Image sprite = getRandomSprite(landSprites);
			cache.put(tile, sprite);
			return sprite;
		} else {
			if (waterSprites.isEmpty()) return null;
			int globalIndex = (int) ((System.currentTimeMillis() / SharedConstants.CAMERA_FRAMERATE * 10) % waterSprites.size());
			int offset = (tile.getX() + tile.getY()) % waterSprites.size();
			int waterIndex = (globalIndex + offset) % waterSprites.size();
			return waterSprites.get(waterIndex);
		}
	}

	// get a random sprite from a list of sprites
	private Image getRandomSprite(List<Image> sprites) {
		if (sprites.isEmpty()) return null;
		return sprites.get(random.nextInt(sprites.size()));
	}
}
