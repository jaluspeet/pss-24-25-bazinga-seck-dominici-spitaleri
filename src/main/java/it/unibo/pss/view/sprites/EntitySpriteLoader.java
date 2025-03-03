package it.unibo.pss.view.sprites;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.SheepEntity;
import it.unibo.pss.model.entity.WolfEntity;
import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.*;

public class EntitySpriteLoader {

	// (baseKey -> list of possible images)
	// e.g. "sheep_move_left" -> [Image1, Image2, ...]
	private final Map<String, List<Image>> variantCache = new HashMap<>();

	// For consistency, each entityId + baseKey 
	// always returns the same random image once chosen.
	// chosenCache.get(entityId).get(baseKey) => single chosen sprite
	private final Map<Integer, Map<String, Image>> chosenCache = new HashMap<>();

	private static final int MAX_VARIANTS = 50;
	private final String relativeBasePath;
	private final Random rng = new Random();

	public EntitySpriteLoader(String relativeBasePath) {
		this.relativeBasePath = relativeBasePath;
		reload();
	}

	/**
	 * Clears both caches. We'll re-load images as needed.
	 */
	public void reload() {
		variantCache.clear();
		chosenCache.clear();
	}

	/**
	 * Return a sprite for this entity & actionKey – e.g. "MOVE_LEFT".
	 * We’ll expect the resource files to be in a subfolder named after the entity type
	 * (sheep, wolf, plant, etc.), e.g.: 
	 *    /sprites/isometric/entity/sheep/sheep_move_left_0.png
	 */
	public Image getEntitySprite(BasicEntity entity, String actionKey) {
		// 1) Figure out subfolder + prefix
		//    e.g. subFolder="sheep", prefix="sheep_"
		String subFolder;
		String prefix;
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
			// fallback entity folder
			subFolder = "entity";
			prefix = "entity_";
		}

		// Build the base key for caches, e.g. "sheep_move_left"
		// We'll do everything lowercase for simplicity
		String baseKey = (prefix + actionKey).toLowerCase(Locale.ROOT);

		// 2) If we've previously chosen a sprite for (entityId, baseKey), re-use it
		Image chosen = getPreviouslyChosen(entity.getId(), baseKey);
		if (chosen != null) {
			return chosen;
		}

		// 3) Attempt to load all variants from subFolder/prefix_actionKey_N.png
		List<Image> variants = variantCache.computeIfAbsent(
				baseKey, 
				k -> loadAllVariants(subFolder, k)
				);
		if (!variants.isEmpty()) {
			chosen = pickRandom(variants);
			storeChoice(entity.getId(), baseKey, chosen);
			return chosen;
		}

		// 4) If none found, fallback to e.g. "sheep_idle"
		String fallbackKey = (prefix + "idle").toLowerCase(Locale.ROOT);
		chosen = getPreviouslyChosen(entity.getId(), fallbackKey);
		if (chosen != null) {
			return chosen;
		}

		List<Image> idleVariants = variantCache.computeIfAbsent(
				fallbackKey,
				k -> loadAllVariants(subFolder, k)
				);
		if (!idleVariants.isEmpty()) {
			chosen = pickRandom(idleVariants);
			storeChoice(entity.getId(), fallbackKey, chosen);
			return chosen;
		}

		// 5) If we also don't have idle => no sprite
		return null;
	}

	/**
	 * Actually load the set of images for subFolder/baseKey_i.png for i in [0..49].
	 * e.g. subFolder="sheep", baseKey="sheep_move_left" => 
	 * path => .../sheep/sheep_move_left_0.png, etc.
	 */
	private List<Image> loadAllVariants(String subFolder, String baseKey) {
		List<Image> result = new ArrayList<>();
		for (int i = 0; i < MAX_VARIANTS; i++) {
			String filename = baseKey + "_" + i + ".png"; 
			// e.g. "sheep_move_left_0.png"
			// Full path => /sprites/isometric/entity/<subFolder>/<filename>
			String fullPath = SpritePathResolver.getPrefix() 
				+ relativeBasePath 
				+ "/" + subFolder 
				+ "/" + filename;
			Image img = loadImage(fullPath);
			if (img != null) {
				result.add(img);
			}
			// (decide whether to break on first not-found 
			// or keep scanning for potential disjoint naming)
		}
		return result;
	}

	private Image loadImage(String path) {
		try (InputStream is = getClass().getResourceAsStream(path)) {
			if (is != null) {
				Image img = new Image(is);
				if (!img.isError()) {
					return img;
				}
			}
		} catch (Exception ignored) { }
		return null;
	}

	private Image pickRandom(List<Image> images) {
		return images.get(rng.nextInt(images.size()));
	}

	private Image getPreviouslyChosen(int entityId, String baseKey) {
		Map<String, Image> map = chosenCache.get(entityId);
		if (map == null) return null;
		return map.get(baseKey);
	}

	private void storeChoice(int entityId, String baseKey, Image chosen) {
		chosenCache.computeIfAbsent(entityId, k -> new HashMap<>()).put(baseKey, chosen);
	}
}
