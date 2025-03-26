package it.unibo.pss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.entity.ActionHandler;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.entity.EntityManager;
import it.unibo.pss.model.entity.PlantEntity;
import it.unibo.pss.model.entity.SheepEntity;
import it.unibo.pss.model.entity.WolfEntity;
import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.WorldManager;

public class SimulationTest {

	private World createWorld(int width, int height) {
		return new World(width, height);
	}

	// Wolf should detect an adjacent sheep and attempt to interact (eat it)
	@Test
	public void testWolfSeeksSheep() {
		World world = createWorld(10, 10);
		WolfEntity wolf = new WolfEntity(world, 5, 5, SharedConstants.WOLF_ENERGY_DEFAULT);
		SheepEntity sheep = new SheepEntity(world, 5, 6, SharedConstants.SHEEP_ENERGY_DEFAULT);
		world.getTile(5, 5).addEntity(wolf);
		world.getTile(5, 6).addEntity(sheep);

		// Wolf should see the sheep and, since it is adjacent, return an INTERACT request.
		BasicEntity.Request request = wolf.getNextRequest();
		assertNotNull(request, "Wolf request should not be null");
		assertEquals(BasicEntity.ActionType.INTERACT, request.type, "Wolf should interact when a sheep is adjacent");
		assertEquals(sheep.getId(), request.targetId, "Wolf should target the sheep's id");
	}

	// Sheep should flee when a wolf is nearby.
	@Test
	public void testSheepFleesFromWolf() {
		World world = createWorld(10, 10);
		
		SheepEntity sheep = new SheepEntity(world, 5, 5, SharedConstants.SHEEP_ENERGY_DEFAULT);
		WolfEntity wolf = new WolfEntity(world, 5, 4, SharedConstants.WOLF_ENERGY_DEFAULT);
		world.getTile(5, 5).addEntity(sheep);
		world.getTile(5, 4).addEntity(wolf);

		BasicEntity.Request request = sheep.getNextRequest();
		assertNotNull(request, "Sheep request should not be null");
		assertEquals(BasicEntity.ActionType.MOVE, request.type, "Sheep should move to flee from a nearby wolf");
		assertNotNull(request.direction, "Fleeing move should have a direction");
	}

	// PlantEntity spawnOffspring should return a new plant with energy 1.
	@Test
	public void testPlantSpawnOffspring() {
		World world = createWorld(10, 10);
		PlantEntity plant = new PlantEntity(world, 3, 3, 1);
		PlantEntity offspring = (PlantEntity) plant.spawnOffspring();
		assertNotNull(offspring, "Offspring should not be null");
		assertTrue(offspring instanceof PlantEntity, "Offspring should be a PlantEntity");
		assertEquals(1, offspring.getEnergy(), "Offspring should start with energy value 1");
	}

	// randomDirection() should return one of the valid directions.
	@Test
	public void testRandomDirectionValidity() {
		World world = createWorld(10, 10);
		SheepEntity sheep = new SheepEntity(world, 0, 0, SharedConstants.SHEEP_ENERGY_DEFAULT);
		BasicEntity.Direction dir = sheep.randomDirection();
		assertNotNull(dir, "Random direction should not be null");
		boolean valid = false;
		for (BasicEntity.Direction d : BasicEntity.Direction.values()) {
			if (d == dir) {
				valid = true;
				break;
			}
		}
		assertTrue(valid, "Random direction should be a valid enum value");
	}

	// sheep should not be allowed to move into a water tile.
	@Test
	public void testInvalidMoveIntoWaterForSheep() {
		World world = createWorld(10, 10);

		world.setTile(5, 5, world.new Tile(5, 5, World.Tile.TileType.LAND));
		world.setTile(6, 5, world.new Tile(6, 5, World.Tile.TileType.WATER));
		SheepEntity sheep = new SheepEntity(world, 5, 5, SharedConstants.SHEEP_ENERGY_DEFAULT);
		world.getTile(5, 5).addEntity(sheep);

		EntityManager em = new EntityManager(world);
		em.addEntity(sheep);
		ActionHandler handler = new ActionHandler(world, em);

		// Request to move RIGHT into water.
		BasicEntity.Request moveRequest = new BasicEntity.Request(BasicEntity.ActionType.MOVE, BasicEntity.Direction.RIGHT);
		ActionHandler.RequestWrapper wrapper = new ActionHandler.RequestWrapper(sheep, moveRequest);
		List<ActionHandler.Action> actions = handler.validateRequests(List.of(wrapper));
		assertTrue(actions.isEmpty(), "Move into water should be rejected for a sheep");
	}

	// Processing a MOVE action should update the entity's position and reduce its energy.
	@Test
	public void testProcessMoveAction() {
		World world = createWorld(10, 10);
	
		world.setTile(2, 2, world.new Tile(2, 2, World.Tile.TileType.LAND));
		world.setTile(3, 2, world.new Tile(3, 2, World.Tile.TileType.LAND));
		SheepEntity sheep = new SheepEntity(world, 2, 2, SharedConstants.SHEEP_ENERGY_DEFAULT);
		world.getTile(2, 2).addEntity(sheep);

		EntityManager em = new EntityManager(world);
		em.addEntity(sheep);
		ActionHandler handler = new ActionHandler(world, em);

		// Create a move right request.
		BasicEntity.Request moveRequest = new BasicEntity.Request(BasicEntity.ActionType.MOVE, BasicEntity.Direction.RIGHT);
		ActionHandler.RequestWrapper wrapper = new ActionHandler.RequestWrapper(sheep, moveRequest);
		List<ActionHandler.Action> actions = handler.validateRequests(List.of(wrapper));
		assertFalse(actions.isEmpty(), "Move request should be valid");
		handler.processActions(actions);

		assertEquals(3, sheep.getX(), "Sheep's X coordinate should update after moving right");
		assertEquals(2, sheep.getY(), "Sheep's Y coordinate should remain the same");
		assertEquals(SharedConstants.SHEEP_ENERGY_DEFAULT - 1, sheep.getEnergy(), "Energy should be reduced by 1 after moving");
	}

	// When two sheep with sufficient energy interact (bazinga), an offspring should be created.
	@Test
	public void testBazingaOffspringCreation() {
		World world = createWorld(10, 10);
	
		world.setTile(4, 4, world.new Tile(4, 4, World.Tile.TileType.LAND));
		world.setTile(5, 4, world.new Tile(5, 4, World.Tile.TileType.LAND));
		world.setTile(4, 5, world.new Tile(4, 5, World.Tile.TileType.LAND));

		// Create two sheep with energy above the bazinga threshold.
		SheepEntity sheep1 = new SheepEntity(world, 4, 4, SharedConstants.SHEEP_ENERGY_DEFAULT + SharedConstants.SHEEP_ENERGY_BAZINGA);
		SheepEntity sheep2 = new SheepEntity(world, 5, 4, SharedConstants.SHEEP_ENERGY_DEFAULT + SharedConstants.SHEEP_ENERGY_BAZINGA);
		world.getTile(4, 4).addEntity(sheep1);
		world.getTile(5, 4).addEntity(sheep2);

		EntityManager em = new EntityManager(world);
		em.addEntity(sheep1);
		em.addEntity(sheep2);
		ActionHandler handler = new ActionHandler(world, em);

		// Simulate an INTERACT request from sheep1 toward sheep2.
		BasicEntity.Request interactRequest = new BasicEntity.Request(BasicEntity.ActionType.INTERACT, sheep2.getId());
		ActionHandler.RequestWrapper wrapper = new ActionHandler.RequestWrapper(sheep1, interactRequest);
		List<ActionHandler.Action> actions = handler.validateRequests(List.of(wrapper));
		handler.processActions(actions);


		assertEquals(3, em.getEntities().size(), "Mating should create an offspring, resulting in 3 entities");
		assertTrue(sheep1.hasBazinged(), "Sheep1 should be marked as having bazinged");
		assertTrue(sheep2.hasBazinged(), "Sheep2 should be marked as having bazinged");
		
		assertEquals(SharedConstants.SHEEP_ENERGY_DEFAULT + SharedConstants.SHEEP_ENERGY_BAZINGA - SharedConstants.SHEEP_ENERGY_BAZINGA, sheep1.getEnergy(), "Sheep1 energy should decrease by the bazinga energy cost");
	}

	// Entities that reach 0 energy should be removed from the simulation.
	@Test
	public void testEntityRemovalOnDeath() {
		World world = createWorld(10, 10);
		EntityManager em = new EntityManager(world);
		
		// Create a sheep with energy 1 so that it dies after energy reduction.
		SheepEntity sheep = new SheepEntity(world, 1, 1, 1);
		em.addEntity(sheep);

		em.updateCycle();
		assertNull(em.getEntityById(sheep.getId()), "A dead sheep should be removed from the entity map");
		assertFalse(em.getEntities().contains(sheep), "The entities list should not include a dead sheep");
	}

	// WorldManager.generateGrid should create a grid of the correct size and include water tiles.
	@Test
	public void testWorldGridGeneration() {
		int width = 20;
		int height = 20;
		World world = WorldManager.generateGrid(width, height);
		assertNotNull(world, "Generated world should not be null");
		assertEquals(width, world.getWidth(), "World width should match the requested value");
		assertEquals(height, world.getHeight(), "World height should match the requested value");

		// Verify that at least one water tile exists (if the water ratio is > 0).
		boolean hasWater = false;
		for (int x = 0; x < width && !hasWater; x++) {
			for (int y = 0; y < height; y++) {
				if (world.getTile(x, y).getType() == World.Tile.TileType.WATER) {
					hasWater = true;
					break;
				}
			}
		}
		assertTrue(hasWater, "The generated world should contain some water tiles");
	}
}

