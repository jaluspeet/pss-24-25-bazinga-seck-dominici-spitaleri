package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.model.world.World.Tile;
import java.util.*;

/**
 * The ActionHandler class is responsible for handling the movement and interaction of entities.
 */
public class ActionHandler {
	private final World world;
	private final EntityManager entityManager;

	/**
	 * Constructor for ActionHandler with the given World and EntityManager.
	 *
	 * @param world the World instance
	 * @param entityManager the EntityManager instance
	 */
	public ActionHandler(World world, EntityManager entityManager) {
		this.world = world;
		this.entityManager = entityManager;
	}


	// REQUEST


	/**
	 * Collects the requests from the given entities.
	 *
	 * @param entities the list of entities
	 */
	public List<RequestWrapper> collectRequests(List<BasicEntity> entities) {
		List<RequestWrapper> requests = new ArrayList<>();
		for (BasicEntity e : entities) {
			if (e.getMovementSpeed() > 0) {
				e.incrementMoveCounter();
				if (e.isTimeToMove()) {
					BasicEntity.Request req = e.getNextRequest();
					
					if (req != null) { requests.add(new RequestWrapper(e, req)); }
					e.resetMoveCounter();
				}
			}
		}
		return requests;
	}

	/**
	 * Validates the given requests and returns the approved actions.
	 *
	 * @param requests the list of requests
	 */
	public List<Action> validateRequests(List<RequestWrapper> requests) {
		List<Action> approvedActions = new ArrayList<>();
		for (RequestWrapper rw : requests) {
			boolean validated = switch (rw.request.type) {
				case MOVE -> validateMove(rw.entity, rw.request.direction);
				case INTERACT -> validateInteract(rw.entity, rw.request.targetId);
			};

			if (validated) { approvedActions.add(new Action(rw.entity, rw.request)); } 
			else { rw.entity.transitionState(false); }
		}
		return approvedActions;
	}

	/**
	 * Processes the given actions.
	 *
	 * @param actions the list of actions
	 */
	public void processActions(List<Action> actions) {
		for (Action action : actions) {
			BasicEntity.Request req = action.request;
			switch (req.type) {
				case MOVE -> processMove(action.entity, req.direction);
				case INTERACT -> processInteract(action.entity, req.targetId);
			}
			action.entity.transitionState(true);
		}
	}


	// VALIDATION


	/**
	 * Validates the movement of the given entity in the given direction.
	 *
	 * @param entity the entity
	 * @param dir the direction
	 * @return true if the movement is valid, false otherwise
	 */
	private boolean validateMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX();
		int newY = entity.getY();
		switch (dir) {
			case UP -> newY--;
			case DOWN -> newY++;
			case LEFT -> newX--;
			case RIGHT -> newX++;
		}

		Tile tile = world.getTile(newX, newY);
		if (tile == null || entity.getEnergy() <= 0) { return false; }
		if (!(entity instanceof WolfEntity) && tile.getType() == Tile.TileType.WATER) { return false; }

		boolean isBlocked = tile.getEntities().stream().anyMatch(e -> !(e instanceof PlantEntity) && e.isAlive());
		return !isBlocked;
	}

	/**
	 * Validates the interaction of the given entity with the entity with the given target ID.
	 *
	 * @param entity the entity
	 * @param targetId the target entity ID
	 * @return true if the interaction is valid, false otherwise
	 */
	private boolean validateInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityManager.getEntityById(targetId);
		if (target == null || !target.isAlive()) { return false; }
		List<Tile> adjacent = getAdjacentTiles(entity.getX(), entity.getY());
		return adjacent.stream().anyMatch(t -> t.getEntities().contains(target));
	}


	// PROCESSING


	/**
	 * Processes the movement of the given entity in the given direction.
	 *
	 * @param entity the entity
	 * @param dir the direction
	 * @return true if the movement was successful, false otherwise
	 */
	private void processMove(BasicEntity entity, BasicEntity.Direction dir) {
		int newX = entity.getX();
		int newY = entity.getY();
		switch (dir) {
			case UP -> newY--;
			case DOWN -> newY++;
			case LEFT -> newX--;
			case RIGHT -> newX++;
		}
		Tile currentTile = world.getTile(entity.getX(), entity.getY());
		Tile targetTile  = world.getTile(newX, newY);

		if (targetTile != null) {
			currentTile.removeEntity(entity);
			entity.setPosition(newX, newY);
			targetTile.addEntity(entity);
			entity.subtractEnergy(1);
		}
	}

	/**
	 * Processes the interaction of the given entity with the entity with the given target ID.
	 *
	 * @param entity the entity
	 * @param targetId the target entity ID
	 */
	private void processInteract(BasicEntity entity, int targetId) {
		BasicEntity target = entityManager.getEntityById(targetId);
		if (target == null || !target.isAlive()) { return; }

		// EAT
		if (entity.getPreyType() != null && entity.getPreyType().isInstance(target)) {
			entityManager.killEntity(target);
			entity.addEnergy(entity.getEnergyRestore());
			return;
		}

		// BAZINGA
		if (entity.getClass().equals(target.getClass()) && entity.getEnergy() >= entity.getEnergyBazinga() && target.getEnergy() >= target.getEnergyBazinga() && !entity.hasBazinged() && !target.hasBazinged()) { 
			spawnOffspring(entity, target);
		}
	}

	/**
	 * Spawns an offspring entity from the given parents.
	 *
	 * @param parent1 the first parent entity
	 * @param parent2 the second parent entity
	 */
	private void spawnOffspring(BasicEntity parent1, BasicEntity parent2) {
		Tile freeTile = findFreeAdjacentTile(parent1.getX(), parent1.getY());
		if (freeTile != null) {
			BasicEntity offspring = parent1.spawnOffspring();
			offspring.setPosition(freeTile.getX(), freeTile.getY());
			entityManager.addEntity(offspring);

			parent1.subtractEnergy(parent1.getEnergyBazinga());
			parent2.subtractEnergy(parent2.getEnergyBazinga());
			parent1.setBazinged();
			parent2.setBazinged();
		}
	}


	// UTILITY


	/**
	 * Finds a free adjacent tile to the given coordinates.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the free adjacent tile, or null if none is found
	 */
	private Tile findFreeAdjacentTile(int x, int y) {
		for (Tile t : getAdjacentTiles(x, y)) { if (t.getEntities().isEmpty()) { return t; } }
		return null;
	}

	/**
	 * Returns a list of adjacent tiles to the given coordinates.
	 *
	 * @param x the x-coordinate
	 * @param y the y-coordinate
	 * @return the list of adjacent tiles
	 */
	private List<Tile> getAdjacentTiles(int x, int y) {
		List<Tile> tiles = new ArrayList<>(Arrays.asList( world.getTile(x, y - 1), world.getTile(x, y + 1), world.getTile(x - 1, y), world.getTile(x + 1, y)));
		tiles.removeIf(Objects::isNull);
		return tiles;
	}

	/**
	 * A wrapper class for a request.
	 */
	public static class RequestWrapper {
		public final BasicEntity entity;
		public final BasicEntity.Request request;

		/**
		 * Constructor for RequestWrapper with the given entity and request.
		 *
		 * @param entity the entity
		 * @param request the request
		 */
		public RequestWrapper(BasicEntity entity, BasicEntity.Request request) {
			this.entity = entity;
			this.request = request;
		}
	}

	/**
	 * A wrapper class for an action.
	 */
	public static class Action {
		public final BasicEntity entity;
		public final BasicEntity.Request request;

		/**
		 * Constructor for Action with the given entity and request.
		 *
		 * @param entity the entity
		 * @param request the request
		 */
		public Action(BasicEntity entity, BasicEntity.Request request) {
			this.entity = entity;
			this.request = request;
		}
	}
}
