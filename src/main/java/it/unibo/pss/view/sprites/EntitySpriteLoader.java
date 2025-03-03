package it.unibo.pss.view.sprites;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.SheepEntity;
import it.unibo.pss.model.entity.WolfEntity;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.*;

public class EntitySpriteLoader {
	private final List<Image> wolfSprites;
	private final List<Image> sheepSprites;
	private final List<Image> plantSprites;
	private final Map<BasicEntity, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();
	private static final int MAX_SPRITES = 50;


	public EntitySpriteLoader(String basePath) {
		wolfSprites = loadSprites(basePath, "wolf");
		sheepSprites = loadSprites(basePath, "sheep");
		plantSprites = loadSprites(basePath, "plant");
	}

	private List<Image> loadSprites(String basePath, String prefix) {
		List<Image> sprites = new ArrayList<>();
		for (int index = 0; index < MAX_SPRITES; index++) {
			String imagePath = basePath + "/" + prefix + "_" + index + ".png";
			InputStream stream = getClass().getResourceAsStream(imagePath);
			if (stream != null) {
				Image img = new Image(stream);
				if (!img.isError()) {
					sprites.add(img);
				}
			}
		}
		return sprites;
	}

	public Image getEntitySprite(BasicEntity entity) {
		return cache.computeIfAbsent(entity, e -> {
			if (e instanceof WolfEntity) return getRandomSprite(wolfSprites);
			if (e instanceof SheepEntity) return getRandomSprite(sheepSprites);
			if (e instanceof PlantEntity) return getRandomSprite(plantSprites);
			return null;
		});
	}

	private Image getRandomSprite(List<Image> sprites) {
		return sprites.isEmpty() ? null : sprites.get(random.nextInt(sprites.size()));
	}
}
