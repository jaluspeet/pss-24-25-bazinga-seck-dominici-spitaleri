package it.unibo.pss.model.world;

import java.util.ArrayList;
import java.util.List;

import it.unibo.pss.model.entity.Entity;

/** Represents the world grid containing tiles. */
public class World {

	private final int width;
	private final int height;
	private final Tile[][] tiles;

	public World(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new Tile[width][height];
		initializeTiles();
	}

	private void initializeTiles() {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				tiles[x][y] = new Tile(x, y, Tile.TileType.LAND);
			}
		}
	}

	public Tile getTile(int x, int y) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return tiles[x][y];
		}
		return null;
	}

	public void setTile(int x, int y, Tile tile) {
		if (x >= 0 && x < width && y >= 0 && y < height) {
			tiles[x][y] = tile;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public List<Tile> getTilesInRange(int x, int y, int range) {
		List<Tile> nearbyTiles = new ArrayList<>();
		for (int dx = -range; dx <= range; dx++) {
			for (int dy = -range; dy <= range; dy++) {
				if (dx == 0 && dy == 0) continue; // Skip the current tile
				Tile tile = getTile(x + dx, y + dy);
				if (tile != null) {
					nearbyTiles.add(tile);
				}
			}
		}
		return nearbyTiles;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				sb.append(tiles[x][y]);
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	/** Represents a single tile in the grid. */
	public class Tile {

		private final int x;
		private final int y;
		private final TileType type;
		private final List<Entity> entities;

		public Tile(int x, int y, TileType type) {
			this.x = x;
			this.y = y;
			this.type = type;
			this.entities = new ArrayList<>();
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public TileType getType() {
			return type;
		}

		public List<Entity> getEntities() {
			return entities;
		}

		public void addEntity(Entity entity) {
			entities.add(entity);
		}

		public void removeEntity(Entity entity) {
			entities.remove(entity);
		}

		@Override
		public String toString() {
			return type == TileType.LAND ? "L" : "W";
		}

		/** Represents the type of a Tile. */
		public enum TileType {
			LAND, WATER
		}
	}
}
