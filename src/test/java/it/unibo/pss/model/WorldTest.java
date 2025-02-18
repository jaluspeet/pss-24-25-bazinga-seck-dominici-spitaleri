package it.unibo.pss.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import it.unibo.pss.model.world.World;

class WorldTest {

	@Test
	void testGridInitialization() {
		World grid = new World(10, 10);
		assertEquals(10, grid.getWidth());
		assertEquals(10, grid.getHeight());
		assertNotNull(grid.getTile(0, 0));
		assertNotNull(grid.getTile(9, 9));
	}

	@Test
	void testTilePosition() {
		World grid = new World(10, 10);
		World.Tile tile = grid.getTile(3, 4);
		assertEquals(3, tile.getX());
		assertEquals(4, tile.getY());
	}

	@Test
	void testTileModification() {
		World grid = new World(10, 10);
		grid.setTile(5, 5, grid.new Tile(5, 5, World.Tile.TileType.WATER));
		assertEquals(World.Tile.TileType.WATER, grid.getTile(5, 5).getType());
	}

	@Test
	void testOutOfBoundsTileAccess() {
		World grid = new World(10, 10);
		assertNull(grid.getTile(-1, 0));
		assertNull(grid.getTile(10, 10));
	}

	@Test
	void testTilesInRange() {
		World grid = new World(10, 10);
		assertEquals(8, grid.getTilesInRange(5, 5, 1).size()); // 8 neighbors
	}
}
