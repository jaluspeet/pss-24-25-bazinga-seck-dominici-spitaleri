package it.unibo.pss.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.WorldGenerator;

class WorldGeneratorTest {

	@Test
	void testGridGeneration() {
		World grid = WorldGenerator.generateGrid(20, 20);
		assertEquals(20, grid.getWidth());
		assertEquals(20, grid.getHeight());
	}

	@Test
	void testGeneratedTerrainContainsWater() {
		World grid = WorldGenerator.generateGrid(20, 20);
		boolean hasWater = false;

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				if (grid.getTile(x, y).getType() == World.Tile.TileType.WATER) {
					hasWater = true;
					break;
				}
			}
		}

		assertTrue(hasWater, "Grid should contain water tiles");
	}

	@Test
	void testGeneratedRivers() {
		World grid = WorldGenerator.generateGrid(20, 20);
		boolean hasContinuousWater = false;

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				World.Tile tile = grid.getTile(x, y);
				if (tile.getType() == World.Tile.TileType.WATER) {
					// Check if there is at least one adjacent water tile
					long waterNeighbors = grid.getTilesInRange(x, y, 1).stream()
							.filter(t -> t.getType() == World.Tile.TileType.WATER)
							.count();
					if (waterNeighbors > 0) {
						hasContinuousWater = true;
						break;
					}
				}
			}
		}

		assertTrue(hasContinuousWater, "Generated rivers should have continuous water connections");
	}
}
