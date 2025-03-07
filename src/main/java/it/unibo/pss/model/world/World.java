package it.unibo.pss.model.world;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import it.unibo.pss.model.entity.BasicEntity;

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

	public void forEachTile(Consumer<Tile> action) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				action.accept(tiles[x][y]);
			}
		}
	}

	public List<Tile> getTilesInRange(int x, int y, int range) {
		List<Tile> nearbyTiles = new ArrayList<>();
		for (int dx = -range; dx <= range; dx++) {
			for (int dy = -range; dy <= range; dy++) {
				if (dx == 0 && dy == 0) continue;
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

	public class Tile {
		private final int x;
		private final int y;
		private final TileType type;
		private final List<BasicEntity> entities;
		private final List<BasicEntity> sortedEntities;
		private boolean needsSorting;

		public Tile(int x, int y, TileType type) {
			this.x = x;
			this.y = y;
			this.type = type;
			this.entities = new ArrayList<>();
			this.sortedEntities = new ArrayList<>();
			this.needsSorting = false;
		}

		public List<BasicEntity> getEntities() {
			return entities;
		}

		public void addEntity(BasicEntity entity) {
			entities.add(entity);
			sortedEntities.add(entity);
			needsSorting = true;
		}

		public void removeEntity(BasicEntity entity) {
			entities.remove(entity);
			sortedEntities.remove(entity);
		}

		public List<BasicEntity> getSortedEntities() {
			if (needsSorting) {
				sortedEntities.sort((a, b) -> {
					// First, sort by zIndex in descending order (higher values last)
					int zComparison = Integer.compare(a.getZIndex(), b.getZIndex());
					if (zComparison != 0) {
						return zComparison; // Ensure higher zIndex appears later
					}
					// If zIndex is the same, sort by ID for consistent ordering
					return Integer.compare(a.getId(), b.getId());
				});
				needsSorting = false;
			}
			return sortedEntities;
		}

		public TileType getType() {
			return type;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		@Override
		public String toString() {
			return type == TileType.LAND ? "L" : "W";
		}

		public enum TileType {
			LAND, WATER
		}
	}
}
