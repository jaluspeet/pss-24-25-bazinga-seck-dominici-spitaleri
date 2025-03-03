package it.unibo.pss.model.entity;

import java.util.*;
import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

public class EntityManager {
	private final List<BasicEntity> entities = new ArrayList<>();
	private final Map<Integer, BasicEntity> entityMap = new HashMap<>();
	private final World grid;
	private final Random random = new Random();

	public EntityManager(World grid) {
		this.grid = grid;
	}

	public void generateInitialEntities() {
		generatePlants(SharedConstants.PLANT_COUNT);
		generateSheep(SharedConstants.SHEEP_COUNT);
		generateWolves(SharedConstants.WOLF_COUNT);
	}

	public void addEntity(BasicEntity entity) {
		entities.add(entity);
		entityMap.put(entity.getId(), entity);
		grid.getTile(entity.getX(), entity.getY()).addEntity(entity);
	}

	public void updateCycle() {
		List<Action> approvedActions = new ArrayList<>();
		List<RequestWrapper> requests = new ArrayList<>();

		// 1) Subtract energy for all non-plants
		for (BasicEntity entity : new ArrayList<>(entities)) {
			if (!(entity instanceof PlantEntity)) {
				entity.subtractEnergy(1);
			}
		}

		// 2) Remove dead (non-plant) entities
		entities.removeIf(entity -> {
			if (!(entity instanceof PlantEntity) && !entity.isAlive()) {
				removeEntity(entity);
				return true;
			}
			return false;
		});

		// 3) Collect requests (movement/interaction) from those who are ready
		for (BasicEntity e : entities) {
			if (e.getMovementSpeed() > 0) {
				e.incrementMoveCounter();
				if (e.isTimeToMove()) {
					BasicEntity.Request req = e.getNextRequest();
					if (req != null) {
						requests.add(new RequestWrapper(e, req));
					}
					e.resetMoveCounter();
				}
			}
		}

		// 4) Validate requested actions
		for (RequestWrapper rw : requests) {
			BasicEntity.Request req = rw.request;
			if (req.type == BasicEntity.ActionType.MOVE) {
				if (validateMove(rw.entity, req.direction)) {
					approvedActions.add(new Action(rw.entity, req));
				}
			} else if (req.type == BasicEntity.ActionType.INTERACT) {
				if (validateInteract(rw.entity, req.targetId)) {
					approvedActions.add(new Action(rw.entity, req));
				}
			}
		}

		// 5) Process approved actions
		for (Action action : approvedActions) {
			BasicEntity.Request req = action.request;
			if (req.type == BasicEntity.ActionType.MOVE) {
				processMove(action.entity, req.direction);
			} else {
				processInteract(action.entity, req.targetId);
			}
			action.entity.transitionState(true);
		}

		// 6) Reset bazinga flag
		entities.forEach(BasicEntity::resetBazinged);

		// 7) Handle plant resurrection
		for (BasicEntity entity : entities) {
			if (entity instanceof PlantEntity) {
				PlantEntity plant = (PlantEntity) entity;
				if (!plant.isAlive()) {
					plant.decrementResurrectionDelay();
					if (plant.getResurrectionDelay() == 0) {
						// re-add to tile if needed
						plant.addEnergy(1);
						World.Tile tile = grid.getTile(plant.getX(), plant.getY());
						if (!tile.getEntities().contains(plant)) {
							tile.addEntity(plant);
						}
					}
				}
			}
		}
	}

	private void removeEntity(BasicEntity entity) {
		grid.getTile(entity.getX(), entity.getY()).removeEntity(entity);
		entityMap.remove(entity.getId());
	}

	private boolean validateMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX();
		int newY = entity.getY();
		switch (dir) {
			case UP:    newY--; break;
			case DOWN:  newY++; break;
			case LEFT:  newX--; break;
			case RIGHT: newX++; break;
		}
		World.Tile tile = grid.getTile(newX, newY);
		if (tile == null || tile.getType() != World.Tile.TileType.LAND || entity.getEnergy() <= 0) {
			return false;
		}
		boolean isBlocked = tile.getEntities().stream()
			.anyMatch(e -> !(e instanceof PlantEntity) && e.isAlive());
		return !isBlocked;
	}

	private void processMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX();
		int newY = entity.getY();
		switch (dir) {
			case UP:    newY--; break;
			case DOWN:  newY++; break;
			case LEFT:  newX--; break;
			case RIGHT: newX++; break;
		}
		World.Tile currentTile = grid.getTile(entity.getX(), entity.getY());
		World.Tile targetTile  = grid.getTile(newX, newY);
		if (targetTile != null) {
			currentTile.removeEntity(entity);
			entity.setPosition(newX, newY);
			targetTile.addEntity(entity);
			entity.subtractEnergy(1); // movement cost
		}
	}

	private boolean validateInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityMap.get(targetId);
		if (target == null || !target.isAlive()) {
			return false;
		}
		List<World.Tile> adjacent = getAdjacentTiles(entity.getX(), entity.getY());
		return adjacent.stream().anyMatch(t -> t.getEntities().contains(target));
	}

	private void processInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityMap.get(targetId);
		if (target == null || !target.isAlive()) {
			return;
		}
		if (entity.getPreyType() != null && entity.getPreyType().isInstance(target)) {
			killEntity(target);
			entity.addEnergy(entity.getEnergyRestore());
			return;
		}
		if (entity.getClass().equals(target.getClass()) &&
				entity.getEnergy() >= entity.getEnergyBazinga() &&
				target.getEnergy() >= target.getEnergyBazinga() &&
				!entity.hasBazinged && !target.hasBazinged) {
			spawnOffspring(entity, target);
				}
	}

	private void killEntity(BasicEntity toKill) {
		removeEntity(toKill);
		toKill.subtractEnergy(toKill.getEnergy());
	}

	private void spawnOffspring(BasicEntity parent1, BasicEntity parent2) {
		World.Tile freeTile = findFreeAdjacentTile(parent1.getX(), parent1.getY());
		if (freeTile != null) {
			BasicEntity offspring = parent1.spawnOffspring();
			offspring.setPosition(freeTile.getX(), freeTile.getY());
			addEntity(offspring);
			parent1.subtractEnergy(parent1.getEnergyBazinga());
			parent2.subtractEnergy(parent2.getEnergyBazinga());
			parent1.setBazinged();
			parent2.setBazinged();
		}
	}

	private World.Tile findFreeAdjacentTile(int x, int y) {
		for (World.Tile t : getAdjacentTiles(x, y)) {
			if (t.getEntities().isEmpty()) {
				return t;
			}
		}
		return null;
	}

	private List<World.Tile> getAdjacentTiles(int x, int y) {
		List<World.Tile> tiles = new ArrayList<>(
				Arrays.asList(
					grid.getTile(x, y - 1),
					grid.getTile(x, y + 1),
					grid.getTile(x - 1, y),
					grid.getTile(x + 1, y)
					)
				);
		tiles.removeIf(Objects::isNull);
		return tiles;
	}

	private void generatePlants(int count) {
		generateEntitiesOfType(PlantEntity.class, count);
	}
	private void generateSheep(int count) {
		generateEntitiesOfType(SheepEntity.class, count);
	}
	private void generateWolves(int count) {
		generateEntitiesOfType(WolfEntity.class, count);
	}

	private void generateEntitiesOfType(Class<? extends BasicEntity> type, int count) {
		int width = grid.getWidth(), height = grid.getHeight();
		int placed = 0;
		while (placed < count) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			World.Tile tile = grid.getTile(x, y);
			if (!tile.getType().equals(World.Tile.TileType.LAND) 
					|| hasEntityOfType(tile, type)) {
				continue;
					}
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
		if (type == PlantEntity.class) {
			return new PlantEntity(grid, x, y, 1);
		} else if (type == SheepEntity.class) {
			return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT);
		} else if (type == WolfEntity.class) {
			return new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT);
		}
		return null;
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
