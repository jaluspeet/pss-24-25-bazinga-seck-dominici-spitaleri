package it.unibo.pss.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.world.World;

class EntityTest {

	static class TestEntity extends BasicEntity {
		public TestEntity(World grid, int x, int y) {
			super(grid, x, y);
		}
	}

	@Test
	void testEntityPlacement() {
		World grid = new World(10, 10);
		BasicEntity entity = new TestEntity(grid, 2, 3);
		assertTrue(grid.getTile(2, 3).getEntities().contains(entity));
	}

	@Test
	void testEntityMovement() {
		World grid = new World(10, 10);
		BasicEntity entity = new TestEntity(grid, 2, 3);

		entity.moveTo(4, 5);
		assertFalse(grid.getTile(2, 3).getEntities().contains(entity));
		assertTrue(grid.getTile(4, 5).getEntities().contains(entity));
	}

	@Test
	void testGetSurroundingTiles() {
		World grid = new World(10, 10);
		BasicEntity entity = new TestEntity(grid, 5, 5);
		assertEquals(8, entity.getSurroundingTiles(1).size());
	}
}
