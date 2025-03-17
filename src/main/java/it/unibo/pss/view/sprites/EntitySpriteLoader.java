package it.unibo.pss.view.sprites;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.SheepEntity;
import it.unibo.pss.model.entity.WolfEntity;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class that loads and caches images for entities.
 */
public class EntitySpriteLoader {
	private final Map<String, List<Image>> variantCache = new ConcurrentHashMap<>();
	private final Map<Integer, Map<String, Image>> chosenCache = new ConcurrentHashMap<>();
	private static final int MAX_VARIANTS = 50;
	private final String relativeBasePath;
	private final Random rng = new Random();

	/** 
	 * Constructor for EntitySpriteLoader.
	 *
	 * @param relativeBasePath the relative base path
	 */
	public EntitySpriteLoader(String relativeBasePath) {
		this.relativeBasePath = relativeBasePath;
		reload();
	}

	/**
	 * Reloads the cache.
	 */
	public void reload() {
		variantCache.clear();
		chosenCache.clear();
	}

	/**
	 * Gets the entity sprite.
	 *
	 * @param entity the entity
	 * @param actionKey the action key
	 * @return the entity sprite
	 */
	public Image getEntitySprite(BasicEntity entity, String actionKey) {
		String subFolder, prefix;
		if (entity instanceof WolfEntity) {
			subFolder = "wolf";
			prefix = "wolf_";
		} else if (entity instanceof SheepEntity) {
			subFolder = "sheep";
			prefix = "sheep_";
		} else if (entity instanceof PlantEntity) {
			subFolder = "plant";
			prefix = "plant_";
		} else {
			subFolder = "entity";
			prefix = "entity_";
		}

		// Try to get a previously chosen image
		String baseKey = (prefix + actionKey).toLowerCase(Locale.ROOT);
		Image chosen = getPreviouslyChosen(entity.getId(), baseKey);
		if (chosen != null) return chosen;

		// Load all variants for this action
		List<Image> variants = variantCache.computeIfAbsent(baseKey, k -> Collections.synchronizedList(loadAllVariants(subFolder, k)));
		if (!variants.isEmpty()) {
			chosen = pickRandom(variants);
			storeChoice(entity.getId(), baseKey, chosen);
			return chosen;
		}

		// If no variants are found, try to get an idle image
		String fallbackKey = (prefix + "idle").toLowerCase(Locale.ROOT);
		chosen = getPreviouslyChosen(entity.getId(), fallbackKey);
		if (chosen != null) return chosen;

		// Load all idle variants
		List<Image> idleVariants = variantCache.computeIfAbsent(fallbackKey, k -> Collections.synchronizedList(loadAllVariants(subFolder, k)));
		if (!idleVariants.isEmpty()) {
			chosen = pickRandom(idleVariants);
			storeChoice(entity.getId(), fallbackKey, chosen);
			return chosen;
		}

		return null;
	}

	/**
	 * Gets the entity sprite for a specific variant.
	 *
	 * @param entity the entity
	 * @return the entity sprite
	 */
	private List<Image> loadAllVariants(String subFolder, String baseKey) {
		List<Image> result = new ArrayList<>();
		for (int i = 0; i < MAX_VARIANTS; i++) {
			String filename = baseKey + "_" + i + ".png"; 
			String fullPath = SpritePathResolver.getPrefix() + relativeBasePath + "/" + subFolder + "/" + filename;
			Image img = loadImage(fullPath);
			if (img == null) {
				break;
			}
			result.add(img);
		}
		return result;
	}

	/**
	 * Loads an image from a path.
	 *
	 * @param path the path
	 * @return the image
	 */
	private Image loadImage(String path) {
		try (InputStream is = getClass().getResourceAsStream(path)) {
			if (is != null) {
				Image img = new Image(is);
				if (!img.isError()) return img;
			}
		} catch (Exception ignored) { }
		return null;
	}

	/**
	 * Picks a random image from a list.
	 *
	 * @param images the images
	 * @return the image
	 */
	private Image pickRandom(List<Image> images) {
		return images.get(rng.nextInt(images.size()));
	}

	/**
	 * Gets the previously chosen image for an entity.
	 */
	private Image getPreviouslyChosen(int entityId, String baseKey) {
		Map<String, Image> map = chosenCache.get(entityId);
		if (map == null) return null;
		return map.get(baseKey);
	}

	/**
	 * Stores a variant choice for an entity.
	 */
	private void storeChoice(int entityId, String baseKey, Image chosen) {
		chosenCache.computeIfAbsent(entityId, k -> new ConcurrentHashMap<>()).put(baseKey, chosen);
	}
}
