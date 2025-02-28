package it.unibo.pss.view.sprites;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.PreyEntity;
import it.unibo.pss.model.entity.PredatorEntity;
import javafx.scene.image.Image;
import java.util.*;

public class EntitySpriteLoader {
	private final List<Image> wolfSprites;
	private final List<Image> sheepSprites;
	private final List<Image> plantSprites;
	private final Map<BasicEntity, Image> cache = new IdentityHashMap<>();
	private final Random random = new Random();

	public EntitySpriteLoader(String basePath) {
		wolfSprites = loadSprites(basePath, "wolf");
		sheepSprites = loadSprites(basePath, "sheep");
		plantSprites = loadSprites(basePath, "plant");
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

	public Image getEntitySprite(BasicEntity entity) {
		return cache.computeIfAbsent(entity, e -> {
			if (e instanceof PredatorEntity) return getRandomSprite(wolfSprites);
			if (e instanceof PreyEntity) return getRandomSprite(sheepSprites);
			if (e instanceof PlantEntity) return getRandomSprite(plantSprites);
			return null;
		});
	}

	private Image getRandomSprite(List<Image> sprites) {
		return sprites.isEmpty() ? null : sprites.get(random.nextInt(sprites.size()));
	}
}
