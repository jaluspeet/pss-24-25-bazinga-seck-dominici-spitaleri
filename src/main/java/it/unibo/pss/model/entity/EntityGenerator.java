package it.unibo.pss.model.entity;

import java.util.Random;
import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

/** Utility class to generate entities on LAND tiles. */
public final class EntityGenerator {
	private static final Random random = new Random();
	
	private EntityGenerator() { }
	
	/** Generates entities using separate counts for plants, preys, and predators. */
	public static void generateEntities(World grid) {
		generatePlants(grid, SharedConstants.PLANT_COUNT);
		generatePreys(grid, SharedConstants.PREY_COUNT);
		generatePredators(grid, SharedConstants.PREDATOR_COUNT);
	}
	
	/** Generates a specified number of plants. */
	private static void generatePlants(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		for (int i = 0; i < count; i++) {
			int x, y;
			do {
				x = random.nextInt(width);
				y = random.nextInt(height);
			} while (grid.getTile(x, y).getType() != World.Tile.TileType.LAND);
			new PlantEntity(grid, x, y);
		}
	}
	
	/** Generates a specified number of preys. */
	private static void generatePreys(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		for (int i = 0; i < count; i++) {
			int x, y;
			do {
				x = random.nextInt(width);
				y = random.nextInt(height);
			} while (grid.getTile(x, y).getType() != World.Tile.TileType.LAND);
			new PreyEntity(grid, x, y);
		}
	}
	
	/** Generates a specified number of predators. */
	private static void generatePredators(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		for (int i = 0; i < count; i++) {
			int x, y;
			do {
				x = random.nextInt(width);
				y = random.nextInt(height);
			} while (grid.getTile(x, y).getType() != World.Tile.TileType.LAND);
			new PredatorEntity(grid, x, y);
		}
	}
}
