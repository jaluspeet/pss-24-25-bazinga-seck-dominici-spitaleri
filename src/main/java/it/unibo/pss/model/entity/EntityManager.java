package it.unibo.pss.model.entity;

import java.util.*;
import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

public class EntityManager {
	private final List<BasicEntity> entities = new ArrayList<>();
	private final Map<Integer, BasicEntity> entityMap = new HashMap<>();
	private final World grid;
	private final Random random = new Random();
	private final InteractionManager interactionManager;

	public EntityManager(World grid) {
		this.grid = grid;
		this.interactionManager = new InteractionManager(grid, entityMap);
	}

	public void addEntity(BasicEntity entity) {
		entities.add(entity);
		entityMap.put(entity.getId(), entity);
		grid.getTile(entity.getX(), entity.getY()).addEntity(entity);
	}

	public void generateEntities() {
		generatePlants(SharedConstants.PLANT_COUNT);
		generateSheep(SharedConstants.SHEEP_COUNT);
		generateWolves(SharedConstants.WOLF_COUNT);
	}

	private void generatePlants(int count) { generateEntitiesOfType(PlantEntity.class, count); }
	private void generateSheep(int count) { generateEntitiesOfType(SheepEntity.class, count); }
	private void generateWolves(int count) { generateEntitiesOfType(WolfEntity.class, count); }

	private void generateEntitiesOfType(Class<? extends BasicEntity> type, int count) {
		int width = grid.getWidth(), height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width), y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			if (!tile.getType().equals(World.Tile.TileType.LAND) || hasEntityOfType(tile, type)) continue;

			BasicEntity entity = createEntity(type, x, y);
			if (entity != null) {
				addEntity(entity);
				placed++;
			}
		}
	}

	private boolean hasEntityOfType(World.Tile tile, Class<? extends BasicEntity> type) {
		return tile.getEntities().stream().anyMatch(type::isInstance);
	}

	private BasicEntity createEntity(Class<? extends BasicEntity> type, int x, int y) {
		if (type == PlantEntity.class) return new PlantEntity(grid, x, y, 1);
		if (type == SheepEntity.class) return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT);
		if (type == WolfEntity.class) return new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT);
		return null;
	}

	public void updateCycle() {
		List<Action> approvedActions = new ArrayList<>();
		List<RequestWrapper> requests = new ArrayList<>();

		// Subtract energy for non-plant entities
		for (BasicEntity entity : new ArrayList<>(entities)) {
			if (!(entity instanceof PlantEntity)) entity.subtractEnergy(1);
		}

		// Remove dead entities from simulation
		entities.removeIf(entity -> {
			if (!(entity instanceof PlantEntity) && !entity.isAlive()) {
				grid.getTile(entity.getX(), entity.getY()).removeEntity(entity);
				entityMap.remove(entity.getId());
				return true;
			}
			return false;
		});

		// Collect movement/interactions based on entity speed
		for (BasicEntity e : entities) {
			if (e.getMovementSpeed() > 0) {
				e.incrementMoveCounter();
				if (e.isTimeToMove()) {
					BasicEntity.Request req = e.getNextRequest();
					if (req != null) requests.add(new RequestWrapper(e, req));
					e.resetMoveCounter();
				}
			}
		}

		// Validate and approve actions
		for (RequestWrapper rw : requests) {
			if (rw.request.type == BasicEntity.ActionType.MOVE && interactionManager.validateMove(rw.entity, rw.request.direction)) {
				approvedActions.add(new Action(rw.entity, rw.request));
			} else if (rw.request.type == BasicEntity.ActionType.INTERACT && interactionManager.validateInteract(rw.entity, rw.request.targetId)) {
				approvedActions.add(new Action(rw.entity, rw.request));
			}
		}

		// Process approved actions
		for (Action action : approvedActions) {
			if (action.request.type == BasicEntity.ActionType.MOVE) {
				interactionManager.processMove(action.entity, action.request.direction);
			} else if (action.request.type == BasicEntity.ActionType.INTERACT) {
				interactionManager.processInteract(action.entity, action.request.targetId);
			}
			action.entity.transitionState(true);
		}

		// Reset bazinga flag
		entities.forEach(BasicEntity::resetBazinged);

		// Process plant resurrection
		for (BasicEntity entity : entities) {
			if (entity instanceof PlantEntity) {
				PlantEntity plant = (PlantEntity) entity;
				if (!plant.isAlive()) {
					plant.decrementResurrectionDelay();
					if (plant.getResurrectionDelay() == 0) {
						plant.addEnergy(1);
						World.Tile tile = grid.getTile(plant.getX(), plant.getY());
						if (!tile.getEntities().contains(plant)) tile.addEntity(plant);
					}
				}
			}
		}
	}

	private static class RequestWrapper {
		BasicEntity entity;
		BasicEntity.Request request;
		RequestWrapper(BasicEntity entity, BasicEntity.Request request) {
			this.entity = entity;
			this.request = request;
		}
	}

	private static class Action {
		BasicEntity entity;
		BasicEntity.Request request;
		Action(BasicEntity entity, BasicEntity.Request request) {
			this.entity = entity;
			this.request = request;
		}
	}

	public List<BasicEntity> getEntities() { return entities; }
}
