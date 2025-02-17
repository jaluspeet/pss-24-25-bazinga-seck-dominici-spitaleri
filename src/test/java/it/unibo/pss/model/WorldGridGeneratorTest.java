package it.unibo.pss.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unibo.pss.model.world.WorldGrid;
import it.unibo.pss.model.world.WorldGridGenerator;

class WorldGridGeneratorTest {

	@Test
	void testGridGeneration() {
		WorldGrid grid = WorldGridGenerator.generateGrid(20, 20);
		assertEquals(20, grid.getWidth());
		assertEquals(20, grid.getHeight());
	}

	@Test
	void testGeneratedTerrainContainsWater() {
		WorldGrid grid = WorldGridGenerator.generateGrid(20, 20);
		boolean hasWater = false;

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				if (grid.getTile(x, y).getType() == WorldGrid.Tile.TileType.WATER) {
					hasWater = true;
					break;
				}
			}
		}

		assertTrue(hasWater, "Grid should contain water tiles");
	}

	@Test
	void testGeneratedRivers() {
		WorldGrid grid = WorldGridGenerator.generateGrid(20, 20);
		boolean hasContinuousWater = false;

		for (int x = 0; x < grid.getWidth(); x++) {
			for (int y = 0; y < grid.getHeight(); y++) {
				WorldGrid.Tile tile = grid.getTile(x, y);
				if (tile.getType() == WorldGrid.Tile.TileType.WATER) {
					// Check if there is at least one adjacent water tile
					long waterNeighbors = grid.getTilesInRange(x, y, 1).stream()
							.filter(t -> t.getType() == WorldGrid.Tile.TileType.WATER)
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
