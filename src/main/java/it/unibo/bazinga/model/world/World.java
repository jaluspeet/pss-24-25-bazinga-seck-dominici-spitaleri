package it.unibo.bazinga.model.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import it.unibo.bazinga.model.entity.BasicEntity;

/**
 * Represents the world map, which is a grid of tiles. Each tile can be either land or water.
 * Each tile can contain entities, which are objects that can be placed on the map.
 * The world map is used to render the game and to simulate the interactions between entities.
 */
public class World {
	private final int width;
	private final int height;
	private final Tile[][] tiles;

	/**
	 * Creates a new world map with the specified width and height.
	 * @param width the width of the world map
	 * @param height the height of the world map
	 */
	public World(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		initializeTiles();
	}

	/**
	 * Initializes all tiles in the world map as land.
	 */
	private void initializeTiles() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y] = new Tile(x, y, Tile.TileType.LAND);
			}
		}
	}

	/**
	 * Returns the tile at the specified coordinates.
	 * @param x the x-coordinate of the tile
	 * @param y the y-coordinate of the tile
	 * @return the tile at the specified coordinates, or null if the coordinates are out of bounds
	 */
	public Tile getTile(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) { return tiles[x][y]; }
		return null;
	}

	/**
	 * Sets the tile at the specified coordinates.
	 * @param x the x-coordinate of the tile
	 * @param y the y-coordinate of the tile
	 * @param tile the new tile to set
	 */
	public void setTile(int x, int y, Tile tile) {
		if (x >= 0 && x < width && y >= 0 && y < height) { tiles[x][y] = tile; }
	}

	public int getWidth() { return width; }
	public int getHeight() { return height; }

	/**
	 * Iterates over all tiles in the world map and applies the specified action to each tile.
	 * @param action the action to apply to each tile
	 */
	public void forEachTile(Consumer<Tile> action) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) { action.accept(tiles[x][y]); }
		}
	}

	/**
	 * Returns a list of all tiles in the world map.
	 * @return a list of all tiles in the world map
	 */
	public List<Tile> getTilesInRange(int x, int y, int range) {
		List<Tile> nearbyTiles = new ArrayList<>();
		for (int dx = -range; dx <= range; dx++) {
			
			for (int dy = -range; dy <= range; dy++) {
				if (dx == 0 && dy == 0) continue;
				Tile tile = getTile(x + dx, y + dy);

				if (tile != null) { nearbyTiles.add(tile); }
			}
		}

		return nearbyTiles;
	}

	/**
	 * Returns a string representation of the world map.
	 * @return a string representation of the world map
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) { sb.append(tiles[x][y]); }
			sb.append("\n");
		}

		return sb.toString();
	}

	/**
	 * Represents a tile in the world map.
	 */
	public static class Tile {
		private final int x;
		private final int y;
		private final TileType type;
		private final List<BasicEntity> entities;
		private final List<BasicEntity> sortedEntities;
		private boolean needsSorting;

		/**
		 * Creates a new tile with the specified coordinates and type.
		 * @param x the x-coordinate of the tile
		 * @param y the y-coordinate of the tile
		 * @param type the type of the tile
		 */
		public Tile(int x, int y, TileType type) {
			this.x = x;
			this.y = y;
			this.type = type;
			this.entities = new ArrayList<>();
			this.sortedEntities = new ArrayList<>();
			this.needsSorting = false;
		}

		public List<BasicEntity> getEntities() { return entities; }

		/**
		 * Adds an entity to the tile.
		 * @param entity the entity to add
		 */
		public void addEntity(BasicEntity entity) {
			entities.add(entity);
			sortedEntities.add(entity);
			needsSorting = true;
		}

		/**
		 * Removes an entity from the tile.
		 * @param entity the entity to remove
		 */
		public void removeEntity(BasicEntity entity) {
			entities.remove(entity);
			sortedEntities.remove(entity);
		}

		/**
		 * Returns a list of entities on the tile, sorted by zIndex in descending order.
		 * @return a list of entities on the tile, sorted by zIndex in descending order
		 */
		public List<BasicEntity> getSortedEntities() {
			if (needsSorting) {
				sortedEntities.sort((a, b) -> {

					// sort by zIndex in descending order (higher values last)
					int zComparison = Integer.compare(a.getZIndex(), b.getZIndex());
					if (zComparison != 0) { return zComparison; }

					// If zIndex is the same, sort by ID
					return Integer.compare(a.getId(), b.getId());
				});
				needsSorting = false;
			}
			return sortedEntities;
		}

		public TileType getType() { return type; }
		public int getX() { return x; }
		public int getY() { return y; }

		@Override
		public String toString() { return type == TileType.LAND ? "L" : "W"; }
		public enum TileType { LAND, WATER }
	}
}
