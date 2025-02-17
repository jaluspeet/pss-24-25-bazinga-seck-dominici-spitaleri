package it.unibo.pss.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WorldGridTest {

	@Test
	void testGridInitialization() {
		WorldGrid grid = new WorldGrid(10, 10);
		assertEquals(10, grid.getWidth());
		assertEquals(10, grid.getHeight());
		assertNotNull(grid.getTile(0, 0));
		assertNotNull(grid.getTile(9, 9));
	}

	@Test
	void testTilePosition() {
		WorldGrid grid = new WorldGrid(10, 10);
		WorldGrid.Tile tile = grid.getTile(3, 4);
		assertEquals(3, tile.getX());
		assertEquals(4, tile.getY());
	}

	@Test
	void testTileModification() {
		WorldGrid grid = new WorldGrid(10, 10);
		grid.setTile(5, 5, grid.new Tile(5, 5, WorldGrid.Tile.TileType.WATER));
		assertEquals(WorldGrid.Tile.TileType.WATER, grid.getTile(5, 5).getType());
	}

	@Test
	void testOutOfBoundsTileAccess() {
		WorldGrid grid = new WorldGrid(10, 10);
		assertNull(grid.getTile(-1, 0));
		assertNull(grid.getTile(10, 10));
	}

	@Test
	void testTilesInRange() {
		WorldGrid grid = new WorldGrid(10, 10);
		assertEquals(8, grid.getTilesInRange(5, 5, 1).size()); // 8 neighbors
	}
}
