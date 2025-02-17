package it.unibo.pss.model.entity;

import java.util.Random;
import it.unibo.pss.model.world.World;

/** Utility class to generate entities on LAND tiles. */
public final class EntityGenerator {
	private static final Random random = new Random();
	
	private EntityGenerator() { }
	
	/** Generates a fixed number of entities randomly on LAND tiles,
	    choosing between PlantEntity and AnimalEntity. */
	public static void generateEntities(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		for (int i = 0; i < count; i++) {
			int x, y;
			do {
				x = random.nextInt(width);
				y = random.nextInt(height);
			} while (grid.getTile(x, y).getType() != World.Tile.TileType.LAND);
			if (random.nextBoolean()) {
				new PlantEntity(grid, x, y);
			} else {
				new AnimalEntity(grid, x, y);
			}
		}
	}
}
