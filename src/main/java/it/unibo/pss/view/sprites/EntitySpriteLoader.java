package it.unibo.pss.view.sprites;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.PreyEntity;
import it.unibo.pss.model.entity.PredatorEntity;
import javafx.scene.image.Image;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.IdentityHashMap;
import java.util.Random;

// load and cache entity sprites from a given path
public class EntitySpriteLoader {
	private final List<Image> wolfSprites;
	private final List<Image> sheepSprites;
	private final List<Image> plantSprites;
	private final Map<BasicEntity, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();

	// constructor for EntitySpriteLoader
	public EntitySpriteLoader(String basePath) {
		wolfSprites = loadSprites(basePath, "wolf");
		sheepSprites = loadSprites(basePath, "sheep");
		plantSprites = loadSprites(basePath, "plant");
	}

	// load sprites from a given path
	private List<Image> loadSprites(String basePath, String prefix) {
		List<Image> sprites = new ArrayList<>();
		int index = 0;
		while (true) {
			String imagePath = basePath + "/" + prefix + "_" + index + ".png";
			try {
				Image img = new Image(getClass().getResourceAsStream(imagePath));
				if (img.getWidth() == 0)
					break;
				sprites.add(img);
				index++;
			} catch (Exception e) {
				break;
			}
		}
		return sprites;
	}

	// get sprite for a given entity
	public Image getEntitySprite(BasicEntity entity) {
		if (cache.containsKey(entity)) {
			return cache.get(entity);
		}
		Image sprite = null;
		if (entity instanceof PredatorEntity) {
			sprite = getRandomSprite(wolfSprites);
		} else if (entity instanceof PreyEntity) {
			sprite = getRandomSprite(sheepSprites);
		} else if (entity instanceof PlantEntity) {
			sprite = getRandomSprite(plantSprites);
		}
		cache.put(entity, sprite);
		return sprite;
	}

	// get a random sprite from a list of sprites
	private Image getRandomSprite(List<Image> sprites) {
		if (sprites.isEmpty())
			return null;
		return sprites.get(random.nextInt(sprites.size()));
	}
}
