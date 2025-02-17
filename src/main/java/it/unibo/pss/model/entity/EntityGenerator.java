package it.unibo.pss.model.entity;

import java.util.Random;
import it.unibo.pss.model.world.WorldGrid;

/** Utility class to generate entities on LAND tiles. */
public final class EntityGenerator {

	private static final Random random = new Random();

	private EntityGenerator() {}

	/** Generates a fixed number of BasicEntity instances randomly on LAND tiles. */
	public static void generateEntities(WorldGrid grid, int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		for (int i = 0; i < count; i++) {
			int x, y;
			do {
				x = random.nextInt(width);
				y = random.nextInt(height);
			} while (grid.getTile(x, y).getType() != WorldGrid.Tile.TileType.LAND);
			new BasicEntity(grid, x, y);
		}
	}
}
