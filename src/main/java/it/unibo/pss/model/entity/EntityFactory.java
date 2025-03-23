package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.World.Tile;
import it.unibo.pss.common.SharedConstants;
import java.util.Random;

/**
 * Factory class for generating entities in the world.
 */
public class EntityFactory {
	private final World world;
	private final Random random = new Random();

	/**
	 * Constructor for EntityFactory.
	 * 
	 * @param world the world in which the entities will be generated
	 */
	public EntityFactory(World world) {
		this.world = world;
	}

	/**
	 * Generates the initial entities in the world.
	 * 
	 * @param entityManager the entity manager to which the entities will be added
	 */
	public void generateInitialEntities(EntityManager entityManager) {
		generatePlants(entityManager, SharedConstants.PLANT_COUNT);
		generateSheep(entityManager, SharedConstants.SHEEP_COUNT);
		generateWolves(entityManager, SharedConstants.WOLF_COUNT);
	}

	/**
	 * Generates a new entity in the world.
	 * 
	 * @param entityManager the entity manager to which the entity will be added
	 * @param type the type of the entity to generate
	 */
	private void generatePlants(EntityManager entityManager, int count) {
		generateEntitiesOfType(PlantEntity.class, count, entityManager);
	}

	/**
	 * Generates a new entity in the world.
	 *
	 * @param entityManager the entity manager to which the entity will be added
	 * @param count the number of entities to generate
	 */
	private void generateSheep(EntityManager entityManager, int count) {
		generateEntitiesOfType(SheepEntity.class, count, entityManager);
	}

	/**
	 * Generates a new entity in the world.
	 *
	 * @param entityManager the entity manager to which the entity will be added
	 * @param count the number of entities to generate
	 */
	private void generateWolves(EntityManager entityManager, int count) {
		generateEntitiesOfType(WolfEntity.class, count, entityManager);
	}

	/**
	 * Generates a new entity in the world.
	 *
	 * @param type the type of the entity to generate
	 * @param count the number of entities to generate
	 * @param entityManager the entity manager to which the entity will be added
	 */
	private void generateEntitiesOfType(Class<? extends BasicEntity> type, int count, 
			EntityManager entityManager) {
		int width = world.getWidth();
		int height = world.getHeight();
		int placed = 0;

		// Try to place the entities in random locations
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			Tile tile = world.getTile(x, y);

			// Skip if the tile is not land or already has an entity of the same type
			if (tile == null || tile.getType() != Tile.TileType.LAND) { continue; }
			if (hasEntityOfType(tile, type)) { continue; }

			BasicEntity entity = createEntity(type, x, y);
			if (entity != null) { entityManager.addEntity(entity); placed++; }
		}
	}

	/**
	 * Checks if a tile has an entity of a specific type.
	 * 
	 * @param tile the tile to check
	 * @param type the type of the entity to check for
	 * @return true if the tile has an entity of the specified type, false otherwise
	 */
	private boolean hasEntityOfType(Tile tile, Class<? extends BasicEntity> type) {
		return tile.getEntities().stream().anyMatch(type::isInstance);
	}

	/**
	 * Creates a new entity of a specific type.
	 * 
	 * @param type the type of the entity to create
	 * @param x the x-coordinate of the entity
	 * @param y the y-coordinate of the entity
	 * @return the newly created entity
	 */
	private BasicEntity createEntity(Class<? extends BasicEntity> type, int x, int y) {
		if (type == PlantEntity.class) { return new PlantEntity(world, x, y, 1); } 
		else if (type == SheepEntity.class) { return new SheepEntity(world, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT); } 
		else if (type == WolfEntity.class) { return new WolfEntity(world, x, y, SharedConstants.WOLF_ENERGY_DEFAULT); }
		return null;
	}
}
