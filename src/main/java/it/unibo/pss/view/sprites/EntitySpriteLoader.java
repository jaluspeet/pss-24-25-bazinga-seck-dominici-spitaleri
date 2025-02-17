package it.unibo.pss.view.sprites;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.AnimalEntity;
import it.unibo.pss.model.entity.PlantEntity;
import javafx.scene.paint.Color;

/** Provides methods to load entity sprites. For now, returns colors based on entity type. */
public class EntitySpriteLoader {
	public Color getEntityColor(BasicEntity entity) {
		if (entity instanceof AnimalEntity) {
			return Color.RED;
		} else if (entity instanceof PlantEntity) {
			return Color.YELLOW;
		}
		return Color.GRAY;
	}
}
