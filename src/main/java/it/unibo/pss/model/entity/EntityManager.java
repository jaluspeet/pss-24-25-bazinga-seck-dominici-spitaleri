package it.unibo.pss.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

	private void generatePlants(int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			if (!tile.getType().equals(World.Tile.TileType.LAND))
				continue;
			boolean hasPlant = tile.getEntities().stream()
				.anyMatch(e -> e instanceof PlantEntity);
			if (hasPlant)
				continue;
			PlantEntity plant = new PlantEntity(grid, x, y, 1);
			addEntity(plant);
			placed++;
		}
	}

	private void generateSheep(int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			if (!tile.getType().equals(World.Tile.TileType.LAND))
				continue;
			boolean hasSheep = tile.getEntities().stream()
				.anyMatch(e -> e instanceof SheepEntity);
			if (hasSheep)
				continue;
			SheepEntity sheep = new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT);
			addEntity(sheep);
			placed++;
		}
	}

	private void generateWolves(int count) {
		int width = grid.getWidth();
		int height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			if (!tile.getType().equals(World.Tile.TileType.LAND))
				continue;
			boolean hasWolf = tile.getEntities().stream()
				.anyMatch(e -> e instanceof WolfEntity);
			if (hasWolf)
				continue;
			WolfEntity wolf = new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT);
			addEntity(wolf);
			placed++;
		}
	}

	public void updateCycle() {
		List<Action> approvedActions = new ArrayList<>();
		List<RequestWrapper> requests = new ArrayList<>();

		// Subtract energy for non-plant entities.
		for (BasicEntity entity : new ArrayList<>(entities)) {
			if (!(entity instanceof PlantEntity))
				entity.subtractEnergy(1);
		}

		// Remove dead non-plant entities from simulation.
		entities.removeIf(entity -> !(entity instanceof PlantEntity) && !entity.isAlive());

		// Collect requests based on movement speed.
		for (BasicEntity e : entities) {
			if (e.getMovementSpeed() > 0) {
				e.incrementMoveCounter();
				if (e.isTimeToMove()) {
					BasicEntity.Request req = e.getNextRequest();
					if (req != null) {
						RequestWrapper rw = new RequestWrapper(e, req);
						requests.add(rw);
					}
					e.resetMoveCounter();
				}
			}
		}

		// Validate requests.
		for (RequestWrapper rw : requests) {
			if (rw.request.type == BasicEntity.ActionType.MOVE) {
				if (interactionManager.validateMove(rw.entity, rw.request.direction))
					approvedActions.add(new Action(rw.entity, rw.request));
			} else if (rw.request.type == BasicEntity.ActionType.INTERACT) {
				if (interactionManager.validateInteract(rw.entity, rw.request.targetId))
					approvedActions.add(new Action(rw.entity, rw.request));
			}
		}

		// Process approved actions.
		for (Action action : approvedActions) {
			if (action.request.type == BasicEntity.ActionType.MOVE) {
				interactionManager.processMove(action.entity, action.request.direction);
				action.entity.transitionState(true);
				// Subtract energy only when a move occurs
				action.entity.subtractEnergy(1);
			} else if (action.request.type == BasicEntity.ActionType.INTERACT) {
				interactionManager.processInteract(action.entity, action.request.targetId);
				action.entity.transitionState(true);
				// Optionally subtract energy for interactions
				action.entity.subtractEnergy(1);
			}
		}

		// Mark failed actions.
		for (RequestWrapper rw : requests) {
			if (!approvedActions.stream().anyMatch(a -> a.entity.getId() == rw.entity.getId()))
				rw.entity.transitionState(false);
		}

		// Reset bazinga flag.
		for (BasicEntity e : entities)
			e.resetBazinged();

		// Process plant resurrection.
		for (BasicEntity entity : entities) {
			if (entity instanceof PlantEntity) {
				PlantEntity plant = (PlantEntity) entity;
				if (!plant.isAlive()) {
					plant.decrementResurrectionDelay();
					if (plant.getResurrectionDelay() == 0) {
						plant.addEnergy(1);
						World.Tile tile = grid.getTile(plant.getX(), plant.getY());
						if (!tile.getEntities().contains(plant))
							tile.addEntity(plant);
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

	public List<BasicEntity> getEntities() {
		return entities;
	}
}
