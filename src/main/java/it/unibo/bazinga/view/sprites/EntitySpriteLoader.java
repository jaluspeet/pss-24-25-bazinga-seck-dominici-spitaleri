package it.unibo.bazinga.view.sprites;

import it.unibo.bazinga.model.entity.BasicEntity;
import it.unibo.bazinga.model.entity.PlantEntity;
import it.unibo.bazinga.model.entity.SheepEntity;
import it.unibo.bazinga.model.entity.WolfEntity;
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
	private final Map<Class<? extends BasicEntity>, SpriteConfig> spriteConfigs = new HashMap<>();

	/**
	 * Constructor initializes the relative base path and maps entity types to their sprite configurations.
	 *
	 * @param relativeBasePath the relative base path for entity sprites.
	 */
	public EntitySpriteLoader(String relativeBasePath) {
		this.relativeBasePath = relativeBasePath;

		// Map specific entity classes to their corresponding sprite configurations.
		spriteConfigs.put(WolfEntity.class, new SpriteConfig("wolf", "wolf_"));
		spriteConfigs.put(SheepEntity.class, new SpriteConfig("sheep", "sheep_"));
		spriteConfigs.put(PlantEntity.class, new SpriteConfig("plant", "plant_"));
		
		// Provide a default configuration for any other BasicEntity type (for testing)
		spriteConfigs.put(BasicEntity.class, new SpriteConfig("entity", "entity_"));
		reload();
	}

	/**
	 * Clears the caches.
	 */
	public void reload() {
		variantCache.clear();
		chosenCache.clear();
	}

	/**
	 * Returns the sprite for the given entity and action.
	 *
	 * @param entity    the entity.
	 * @param actionKey the action key.
	 * @return the sprite image.
	 */
	public Image getEntitySprite(BasicEntity entity, String actionKey) {
		SpriteConfig config = getSpriteConfigForEntity(entity);
		String baseKey = (config.getPrefix() + actionKey).toLowerCase(Locale.ROOT);

		Image chosen = getPreviouslyChosen(entity.getId(), baseKey);
		if (chosen != null) return chosen;

		List<Image> variants = variantCache.computeIfAbsent(baseKey, k -> Collections.synchronizedList(loadAllVariants(config.getSubFolder(), k)));
		if (!variants.isEmpty()) {
			chosen = pickRandom(variants);
			storeChoice(entity.getId(), baseKey, chosen);
			return chosen;
		}

		// Fallback to idle image if no action variants are found.
		String fallbackKey = (config.getPrefix() + "idle").toLowerCase(Locale.ROOT);
		chosen = getPreviouslyChosen(entity.getId(), fallbackKey);
		if (chosen != null) return chosen;

		List<Image> idleVariants = variantCache.computeIfAbsent(fallbackKey, k -> Collections.synchronizedList(loadAllVariants(config.getSubFolder(), k)));
		if (!idleVariants.isEmpty()) {
			chosen = pickRandom(idleVariants);
			storeChoice(entity.getId(), fallbackKey, chosen);
			return chosen;
		}

		return null;
	}

	/**
	 * Retrieves the sprite configuration for an entity based on its class.
	 *
	 * @param entity the entity.
	 * @return the corresponding SpriteConfig.
	 */
	private SpriteConfig getSpriteConfigForEntity(BasicEntity entity) {
		
		// look for match
		SpriteConfig config = spriteConfigs.get(entity.getClass());
		if (config != null) {
			return config;
		}
		
		// fallback to default
		return spriteConfigs.get(BasicEntity.class);
	}

	/**
	 * Loads all sprite variants for the given subfolder and base key.
	 *
	 * @param subFolder the subfolder name.
	 * @param baseKey   the base key.
	 * @return a list of images.
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
	 * Loads an image from the given path.
	 *
	 * @param path the image path.
	 * @return the loaded image, or null if not found.
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
	 * Picks a random image from the list.
	 *
	 * @param images the list of images.
	 * @return a randomly chosen image.
	 */
	private Image pickRandom(List<Image> images) {
		return images.get(rng.nextInt(images.size()));
	}

	private Image getPreviouslyChosen(int entityId, String baseKey) {
		Map<String, Image> map = chosenCache.get(entityId);
		if (map == null) return null;
		return map.get(baseKey);
	}

	private void storeChoice(int entityId, String baseKey, Image chosen) {
		chosenCache.computeIfAbsent(entityId, _ -> new ConcurrentHashMap<>()).put(baseKey, chosen);
	}
}

