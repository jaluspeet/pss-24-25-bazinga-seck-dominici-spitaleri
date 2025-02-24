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

	private static void generatePlants(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			boolean hasPlant = tile.getEntities().stream().anyMatch(e -> e instanceof PlantEntity);
			if (tile.getType() == World.Tile.TileType.LAND && !hasPlant) {
				new PlantEntity(grid, x, y);
				placed++;
			}
		}
	}

	private static void generatePreys(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			boolean hasPrey = tile.getEntities().stream().anyMatch(e -> e instanceof PreyEntity);
			if (tile.getType() == World.Tile.TileType.LAND && !hasPrey) {
				new PreyEntity(grid, x, y);
				placed++;
			}
		}
	}

	private static void generatePredators(World grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			boolean hasPredator = tile.getEntities().stream().anyMatch(e -> e instanceof PredatorEntity);
			if (tile.getType() == World.Tile.TileType.LAND && !hasPredator) {
				new PredatorEntity(grid, x, y);
				placed++;
			}
		}
	}
}
