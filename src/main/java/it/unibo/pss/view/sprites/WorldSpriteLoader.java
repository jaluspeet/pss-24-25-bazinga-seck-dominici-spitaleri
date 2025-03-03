package it.unibo.pss.view.sprites;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;
import javafx.scene.image.Image;
import java.util.*;

public class WorldSpriteLoader {
	private List<Image> landSprites;
	private List<Image> waterSprites;
	private final Map<World.Tile, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();
	private final String relativeBasePath;

	public WorldSpriteLoader(String relativeBasePath) {
		this.relativeBasePath = relativeBasePath;
		reload();
	}

	public void reload() {
		String basePath = SpritePathResolver.getPrefix() + relativeBasePath;
		landSprites = loadSprites(basePath, "land");
		waterSprites = loadSprites(basePath, "water");
		cache.clear();
	}

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

	public Image getTileSprite(World.Tile tile) {
		if (tile.getType() == World.Tile.TileType.LAND) {
			return cache.computeIfAbsent(tile, t -> getRandomSprite(landSprites));
		}
		if (waterSprites.isEmpty()) return null;
		int globalIndex = (int) ((System.currentTimeMillis() / (SharedConstants.CAMERA_FRAMERATE * 10)) % waterSprites.size());
		return waterSprites.get((globalIndex + (tile.getX() + tile.getY())) % waterSprites.size());
	}

	private Image getRandomSprite(List<Image> sprites) {
		return sprites.isEmpty() ? null : sprites.get(random.nextInt(sprites.size()));
	}
}
