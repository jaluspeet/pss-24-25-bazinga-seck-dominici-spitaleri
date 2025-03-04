package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.World.Tile;
import it.unibo.pss.common.SharedConstants;
import java.util.Random;

public class EntityFactory {
	private final World world;
	private final Random random = new Random();

	public EntityFactory(World world) {
		this.world = world;
	}

	public void generateInitialEntities(EntityManager entityManager) {
		generatePlants(entityManager, SharedConstants.PLANT_COUNT);
		generateSheep(entityManager, SharedConstants.SHEEP_COUNT);
		generateWolves(entityManager, SharedConstants.WOLF_COUNT);
	}

	private void generatePlants(EntityManager entityManager, int count) {
		generateEntitiesOfType(PlantEntity.class, count, entityManager);
	}

	private void generateSheep(EntityManager entityManager, int count) {
		generateEntitiesOfType(SheepEntity.class, count, entityManager);
	}

	private void generateWolves(EntityManager entityManager, int count) {
		generateEntitiesOfType(WolfEntity.class, count, entityManager);
	}

	private void generateEntitiesOfType(Class<? extends BasicEntity> type, int count, 
			EntityManager entityManager) {
		int width = world.getWidth();
		int height = world.getHeight();
		int placed = 0;

		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			Tile tile = world.getTile(x, y);

			if (tile == null || tile.getType() != Tile.TileType.LAND) {
				continue;
			}
			if (hasEntityOfType(tile, type)) {
				continue;
			}

			BasicEntity entity = createEntity(type, x, y);
			if (entity != null) {
				entityManager.addEntity(entity);
				placed++;
			}
		}
	}

	private boolean hasEntityOfType(Tile tile, Class<? extends BasicEntity> type) {
		return tile.getEntities().stream().anyMatch(type::isInstance);
	}

	private BasicEntity createEntity(Class<? extends BasicEntity> type, int x, int y) {
		if (type == PlantEntity.class) {
			return new PlantEntity(world, x, y, 1);
		} else if (type == SheepEntity.class) {
			return new SheepEntity(world, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT);
		} else if (type == WolfEntity.class) {
			return new WolfEntity(world, x, y, SharedConstants.WOLF_ENERGY_DEFAULT);
		}
		return null;
	}
}
