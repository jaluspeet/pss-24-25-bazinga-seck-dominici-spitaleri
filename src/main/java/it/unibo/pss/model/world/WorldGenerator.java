package it.unibo.pss.model.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

/** Utility class to generate and initialize the world grid. */
public final class WorldGenerator {

	private static final Random random = new Random();
	private static final List<World.Tile> lakeCenters = new ArrayList<>();

	// Water generation parameters
	private static final double MAP_WATER_RATIO = 0.4;
	private static final double LAKE_WATER_RATIO = 0.7;
	private static final int DEFAULT_NUM_LAKES = 6;

	private WorldGenerator() {}

	/** Generates a new world grid with terrain generation. */
	public static World generateGrid(int width, int height) {
		World grid = new World(width, height);
		initializeLand(grid);
		lakeCenters.clear();
		
		int totalTiles = width * height;
		int targetWaterTiles = (int) (totalTiles * MAP_WATER_RATIO);
		int targetLakeTiles = (int) (targetWaterTiles * LAKE_WATER_RATIO);
		int targetRiverTiles = targetWaterTiles - targetLakeTiles;
		
		// Determine target area per lake and generate a fixed number of lakes
		int lakeTargetSize = targetLakeTiles / DEFAULT_NUM_LAKES;
		for (int i = 0; i < DEFAULT_NUM_LAKES; i++) {
			int centerX = random.nextInt(width - 4) + 2;  // avoid edges
			int centerY = random.nextInt(height - 4) + 2;
			int actualLakeSize = (int) (lakeTargetSize * (0.8 + 0.4 * random.nextDouble()));
			createLake(grid, centerX, centerY, actualLakeSize);
			lakeCenters.add(grid.getTile(centerX, centerY));
		}
		
		// Generate rivers connecting adjacent lakes with target sizes
		if (lakeCenters.size() > 1) {
			Collections.shuffle(lakeCenters);
			int numRivers = lakeCenters.size() - 1;
			int riverTargetSize = targetRiverTiles / numRivers;
			for (int i = 0; i < lakeCenters.size() - 1; i++) {
				World.Tile start = lakeCenters.get(i);
				World.Tile end = lakeCenters.get(i + 1);
				createRiver(grid, start.getX(), start.getY(), end.getX(), end.getY(), riverTargetSize);
			}
		}
		
		return grid;
	}

	/** Initializes the grid with land tiles. */
	private static void initializeLand(World grid) {
		IntStream.range(0, grid.getWidth())
			.forEach(x -> IntStream.range(0, grid.getHeight())
				.forEach(y -> grid.setTile(x, y, grid.new Tile(x, y, World.Tile.TileType.LAND))));
	}

	/**
	 * Creates a lake using a flood-fill expansion from (centerX, centerY) until targetSize is reached.
	 * Returns the number of tiles filled.
	 */
	private static int createLake(World grid, int centerX, int centerY, int targetSize) {
		Queue<World.Tile> queue = new LinkedList<>();
		queue.add(grid.getTile(centerX, centerY));
		Set<World.Tile> lakeTiles = new HashSet<>();
		while (!queue.isEmpty() && lakeTiles.size() < targetSize) {
			World.Tile tile = queue.poll();
			if (tile == null || lakeTiles.contains(tile))
				continue;
			grid.setTile(tile.getX(), tile.getY(), grid.new Tile(tile.getX(), tile.getY(), World.Tile.TileType.WATER));
			lakeTiles.add(tile);
			List<int[]> directions = List.of(new int[]{1, 0}, new int[]{-1, 0}, new int[]{0, 1}, new int[]{0, -1});
			directions.stream()
				.filter(dir -> random.nextDouble() < 0.8)
				.map(dir -> grid.getTile(tile.getX() + dir[0], tile.getY() + dir[1]))
				.forEach(queue::add);
		}
		return lakeTiles.size();
	}

	/**
	 * Creates a river connecting (startX, startY) and (endX, endY), stopping when targetSize is reached.
	 * Returns the number of tiles filled.
	 */
	private static int createRiver(World grid, int startX, int startY, int endX, int endY, int targetSize) {
		int added = 0;
		int x = startX, y = startY;
		while ((x != endX || y != endY) && added < targetSize) {
			if (x < 0 || x >= grid.getWidth() || y < 0 || y >= grid.getHeight())
				break;
			grid.setTile(x, y, grid.new Tile(x, y, World.Tile.TileType.WATER));
			added++;
			if (random.nextBoolean()) {
				if (x < endX) x++;
				else if (x > endX) x--;
			}
			if (random.nextBoolean()) {
				if (y < endY) y++;
				else if (y > endY) y--;
			}
		}
		return added;
	}
}
