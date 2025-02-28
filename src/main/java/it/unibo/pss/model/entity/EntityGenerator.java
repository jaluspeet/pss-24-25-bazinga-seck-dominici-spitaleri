package it.unibo.pss.model.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

/** Manager for all entities */
public class EntityGenerator {
	private final List<BasicEntity> entities = new ArrayList<>();
	private final Map<Integer, BasicEntity> entityMap = new HashMap<>();
	private final World grid;
	private final Random random = new Random();

	public EntityGenerator(World grid) {
		this.grid = grid;
	}

	public void addEntity(BasicEntity entity) {
		entities.add(entity);
		entityMap.put(entity.getId(), entity);
		grid.getTile(entity.getX(), entity.getY()).addEntity(entity);
	}

	/**
	 * Generates initial entities on LAND tiles.
	 * Only counts placements on LAND (skips water) and avoids duplicate placement for a type.
	 */
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
		// Collect requests from all entities
		for (BasicEntity e : entities) {
			RequestWrapper rw = new RequestWrapper(e, e.getNextRequest());
			requests.add(rw);
		}
		// Validate requests
		for (RequestWrapper rw : requests) {
			if (rw.request.type == BasicEntity.ActionType.MOVE) {
				if (validateMove(rw.entity, rw.request.direction))
					approvedActions.add(new Action(rw.entity, rw.request));
			} else if (rw.request.type == BasicEntity.ActionType.INTERACT) {
				if (validateInteract(rw.entity, rw.request.targetId))
					approvedActions.add(new Action(rw.entity, rw.request));
			}
		}
		// Process approved requests
		for (Action action : approvedActions) {
			if (action.request.type == BasicEntity.ActionType.MOVE) {
				processMove(action.entity, action.request.direction);
				action.entity.transitionState(true);
			} else if (action.request.type == BasicEntity.ActionType.INTERACT) {
				processInteract(action.entity, action.request.targetId);
				action.entity.transitionState(true);
			}
		}
		// Mark failed actions
		for (RequestWrapper rw : requests) {
			if (!approvedActions.stream().anyMatch(a -> a.entity.getId() == rw.entity.getId()))
				rw.entity.transitionState(false);
		}
		// Reset mating flag for next update
		for (BasicEntity e : entities)
			e.resetMated();
		// Process plant resurrection
		processPlantResurrection();
	}

	private boolean validateMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX(), newY = entity.getY();
		switch(dir) {
			case UP: newY--; break;
			case DOWN: newY++; break;
			case LEFT: newX--; break;
			case RIGHT: newX++; break;
		}
		World.Tile tile = grid.getTile(newX, newY);
		if (tile == null || !tile.getType().equals(World.Tile.TileType.LAND))
			return false;
		return tile.getEntities().isEmpty() && entity.getEnergy() > 0;
	}

	private void processMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX(), newY = entity.getY();
		switch(dir) {
			case UP: newY--; break;
			case DOWN: newY++; break;
			case LEFT: newX--; break;
			case RIGHT: newX++; break;
		}
		World.Tile currentTile = grid.getTile(entity.getX(), entity.getY());
		World.Tile targetTile = grid.getTile(newX, newY);
		if (targetTile != null) {
			currentTile.removeEntity(entity);
			entity.setPosition(newX, newY);
			targetTile.addEntity(entity);
			entity.subtractEnergy(1);
		}
	}

	private boolean validateInteract(BasicEntity entity, int targetId) {
		List<World.Tile> adjacent = getAdjacentTiles(entity.getX(), entity.getY());
		for (World.Tile t : adjacent)
			for (BasicEntity other : t.getEntities())
				if (other.getId() == targetId)
					return true;
		return false;
	}

	private void processInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityMap.get(targetId);
		if (target == null)
			return;
		// Prey interaction: if target is of the expected prey type
		if (entity.getPreyType() != null && target.getClass().equals(entity.getPreyType())) {
			if (!target.isAlive())
				return;
			killEntity(target);
		}
		// Mate interaction: if same type and target has sufficient energy and hasn’t mated yet
		if (entity.getClass().equals(target.getClass()) &&
				target.getEnergy() >= SharedConstants.SHEEP_ENERGY_BAZINGA &&
				!target.hasMated) {
			World.Tile freeTile = findFreeAdjacentTile(entity.getX(), entity.getY());
			if (freeTile != null) {
				BasicEntity offspring = entity.spawnOffspring();
				addEntity(offspring);
				entity.subtractEnergy(SharedConstants.SHEEP_ENERGY_BAZINGA);
				target.subtractEnergy(SharedConstants.SHEEP_ENERGY_BAZINGA);
				entity.setMated();
				target.setMated();
			}
		}
	}

	private List<World.Tile> getAdjacentTiles(int x, int y) {
		List<World.Tile> tiles = new ArrayList<>();
		tiles.add(grid.getTile(x, y - 1));
		tiles.add(grid.getTile(x, y + 1));
		tiles.add(grid.getTile(x - 1, y));
		tiles.add(grid.getTile(x + 1, y));
		tiles.removeIf(t -> t == null);
		return tiles;
	}

	private World.Tile findFreeAdjacentTile(int x, int y) {
		for (World.Tile t : getAdjacentTiles(x, y))
			if (t.getEntities().isEmpty())
				return t;
		return null;
	}

	private void killEntity(BasicEntity entity) {
		World.Tile tile = grid.getTile(entity.getX(), entity.getY());
		tile.removeEntity(entity);
		entity.energy = 0;
	}

	private void processPlantResurrection() {
		List<PlantEntity> toResurrect = new ArrayList<>();
		for (BasicEntity e : entities) {
			if (e instanceof PlantEntity) {
				PlantEntity p = (PlantEntity) e;
				if (p.isDead()) {
					p.decrementResurrectionCounter();
					if (p.getResurrectionCounter() <= 0)
						toResurrect.add(p);
				}
			}
		}
		for (PlantEntity p : toResurrect) {
			World.Tile freeTile = findFreeAdjacentTile(p.getX(), p.getY());
			if (freeTile == null)
				freeTile = grid.getTile(p.getX(), p.getY());
			BasicEntity newPlant = p.spawnOffspring();
			addEntity(newPlant);
			entities.remove(p);
			entityMap.remove(p.getId());
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
