package it.unibo.pss.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/** Utility class to generate and initialize the world grid. */
public final class WorldGridGenerator {

	private static final Random random = new Random();
	private static final List<WorldGrid.Tile> lakeCenters = new ArrayList<>();

	private WorldGridGenerator() {}

	/** Generates a new world grid with terrain generation. */
	public static WorldGrid generateGrid(int width, int height) {
		WorldGrid grid = new WorldGrid(width, height);
		initializeLand(grid);
		generateLakes(grid, 3, 6, 10, 40);
		generateRivers(grid);
		return grid;
	}

	/** initialize the grid with land tiles. */
	private static void initializeLand(WorldGrid grid) {
		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				grid.setTile(x, y, grid.new Tile(x, y, WorldGrid.Tile.TileType.LAND));
			}
		}
	}

	/** generate lakes of various sizes. */
	private static void generateLakes(WorldGrid grid, int minLakes, int maxLakes, int minSize, int maxSize) {
		int lakeCount = random.nextInt(maxLakes - minLakes + 1) + minLakes;

		for (int i = 0; i < lakeCount; i++) {
			int centerX = random.nextInt(grid.getWidth() - 4) + 2; // Avoid edges
			int centerY = random.nextInt(grid.getHeight() - 4) + 2;
			int lakeSize = random.nextInt(maxSize - minSize + 1) + minSize;

			createLake(grid, centerX, centerY, lakeSize);
			lakeCenters.add(grid.getTile(centerX, centerY));
		}
	}

	/** create a lake. */
	private static void createLake(WorldGrid grid, int centerX, int centerY, int size) {
		Queue<WorldGrid.Tile> queue = new LinkedList<>();
		queue.add(grid.getTile(centerX, centerY));
		Set<WorldGrid.Tile> lakeTiles = new HashSet<>();

		while (!queue.isEmpty() && lakeTiles.size() < size) {
			WorldGrid.Tile tile = queue.poll();
			if (tile == null || lakeTiles.contains(tile)) continue;

			grid.setTile(tile.getX(), tile.getY(), grid.new Tile(tile.getX(), tile.getY(), WorldGrid.Tile.TileType.WATER));
			lakeTiles.add(tile);

			// Expand randomly in all directions
			if (random.nextDouble() < 0.8) queue.add(grid.getTile(tile.getX() + 1, tile.getY()));
			if (random.nextDouble() < 0.8) queue.add(grid.getTile(tile.getX() - 1, tile.getY()));
			if (random.nextDouble() < 0.8) queue.add(grid.getTile(tile.getX(), tile.getY() + 1));
			if (random.nextDouble() < 0.8) queue.add(grid.getTile(tile.getX(), tile.getY() - 1));
		}
	}

	/** generate rivers that connect lakes. */
	private static void generateRivers(WorldGrid grid) {
		if (lakeCenters.size() < 2) return; // Need at least 2 lakes

		Collections.shuffle(lakeCenters);

		for (int i = 0; i < lakeCenters.size() - 1; i++) {
			WorldGrid.Tile start = lakeCenters.get(i);
			WorldGrid.Tile end = lakeCenters.get(i + 1);
			createRiver(grid, start.getX(), start.getY(), end.getX(), end.getY());
		}
	}

	/** create a river flowing between two lakes. */
	private static void createRiver(WorldGrid grid, int startX, int startY, int endX, int endY) {
		int x = startX, y = startY;

		while (x != endX || y != endY) {
			if (x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight()) break;

			grid.setTile(x, y, grid.new Tile(x, y, WorldGrid.Tile.TileType.WATER));

			if (random.nextBoolean()) {
				if (x < endX) x++;
				else if (x > endX) x--;
			}
			if (random.nextBoolean()) {
				if (y < endY) y++;
				else if (y > endY) y--;
			}
		}
	}
}
