package it.unibo.pss.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

	static class TestEntity extends Entity {
		public TestEntity(WorldGrid grid, int x, int y) {
			super(grid, x, y);
		}
	}

	@Test
	void testEntityPlacement() {
		WorldGrid grid = new WorldGrid(10, 10);
		Entity entity = new TestEntity(grid, 2, 3);
		assertTrue(grid.getTile(2, 3).getEntities().contains(entity));
	}

	@Test
	void testEntityMovement() {
		WorldGrid grid = new WorldGrid(10, 10);
		Entity entity = new TestEntity(grid, 2, 3);

		entity.moveTo(4, 5);
		assertFalse(grid.getTile(2, 3).getEntities().contains(entity));
		assertTrue(grid.getTile(4, 5).getEntities().contains(entity));
	}

	@Test
	void testGetSurroundingTiles() {
		WorldGrid grid = new WorldGrid(10, 10);
		Entity entity = new TestEntity(grid, 5, 5);
		assertEquals(8, entity.getSurroundingTiles(1).size());
	}
}
