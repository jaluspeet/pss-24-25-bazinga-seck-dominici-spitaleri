package it.unibo.pss.model.entity;

import java.util.function.Function;

import it.unibo.pss.model.world.World;

/**
 * BasicEntity is the abstract class that represents the entities in the simulation.
 * It contains the basic attributes and methods that are common to all entities.
 * It also contains the abstract methods that must be implemented by the subclasses.
 */
public abstract class BasicEntity {
	protected final World grid;
	private final int id;
	protected int x, y;
	protected int energy;
	protected boolean hasBazinged;
	protected State currentState;
	private static int nextId = 0;
	private int moveCounter = 0;
	
	protected int sightRange;
	protected int energyBazinga;
	protected int energyHungry;
	protected int energyRestore;
	protected int movementSpeed;
	protected int zIndex;

	/**
	 * Constructor for the BasicEntity class.
	 * @param grid The world grid in which the entity is located.
	 * @param x The x-coordinate of the entity.
	 * @param y The y-coordinate of the entity.
	 * @param initialEnergy The initial energy of the entity.
	 */
	public BasicEntity(World grid, int x, int y, int initialEnergy) {
		this.grid = grid;
		this.id = nextId++;
		this.x = x;
		this.y = y;
		this.energy = initialEnergy;
		this.hasBazinged = false;
		this.currentState = initialState();
		this.zIndex = 0;
	}


	// Abstract methods that must be implemented by the subclasses
	protected abstract State initialState();
	public abstract Request getNextRequest();
	public abstract void transitionState(boolean actionSuccess);
	public abstract Class<? extends BasicEntity> getPreyType();
	public abstract Class<? extends BasicEntity> getPredatorType();
	public abstract BasicEntity spawnOffspring();

	// Getters
	public int getId() { return id; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getEnergy() { return energy; }
	public int getZIndex() { return zIndex; }
	public boolean isAlive() { return energy > 0; }
	public int getSightRange() { return sightRange; }
	public int getEnergyBazinga() { return energyBazinga; }
	public int getEnergyHungry() { return energyHungry; }
	public int getEnergyRestore() { return energyRestore; }
	public int getMovementSpeed() { return movementSpeed; }

	// Setters
	public void setPosition(int newX, int newY) { x = newX; y = newY; }
	public void addEnergy(int amount) { energy += amount; }
	public void subtractEnergy(int amount) { energy = Math.max(0, energy - amount); }
	public void setSightRange(int sightRange) { this.sightRange = sightRange; }
	public void setEnergyBazinga(int energyBazinga) { this.energyBazinga = energyBazinga; }
	public void setEnergyHungry(int energyHungry) { this.energyHungry = energyHungry; }
	public void setEnergyRestore(int energyRestore) { this.energyRestore = energyRestore; }
	public void setMovementSpeed(int movementSpeed) { this.movementSpeed = movementSpeed; }


	/**
	 * Method that updates the entity's state and energy.
	 * @param target The entity that the current entity is interacting with.
	 */
	protected Request moveOrInteract(BasicEntity target) {
		int dist = Math.abs(x - target.getX()) + Math.abs(y - target.getY());
		return dist <= 1 ? new Request(ActionType.INTERACT, target.getId()) : moveToward(target);
	}

	/**
	 * Method that moves the entity toward the target entity.
	 * @param target The entity that the current entity is moving toward.
	 */
	protected Request moveToward(BasicEntity target) {
		int dx = target.getX() - x;
		int dy = target.getY() - y;
		Direction moveDir = (Math.abs(dx) >= Math.abs(dy)) ? (dx > 0 ? Direction.RIGHT : Direction.LEFT) : (dy > 0 ? Direction.DOWN : Direction.UP);
		return new Request(ActionType.MOVE, moveDir);
	}

	/**
	 * Method that moves the entity away from the target entity.
	 * @param target The entity that the current entity is moving away from.
	 */
	protected Request moveAway(BasicEntity entity) {
		Direction bestDirection = null;
		int maxDist = Math.abs(x - entity.getX()) + Math.abs(y - entity.getY());

		// Find the direction that maximizes the distance from the target entity
		for (Direction dir : Direction.values()) {
			int newX = x, newY = y;
			switch (dir) {
				case UP -> newY--;
				case DOWN -> newY++;
				case LEFT -> newX--;
				case RIGHT -> newX++;
			}
			int newDist = Math.abs(newX - entity.getX()) + Math.abs(newY - entity.getY());
			if (newDist > maxDist) {
				maxDist = newDist;
				bestDirection = dir;
			}
		}

		return new Request(ActionType.MOVE, (bestDirection != null) ? bestDirection : randomDirection());
	}

	/**
	 * Method that moves the entity in a random direction.
	 */
	public Direction randomDirection() {
		Direction[] dirs = Direction.values();
		return dirs[(int) (Math.random() * dirs.length)];
	}

	/**
	 * Method that finds the nearest entity of a given type within a given range.
	 * @param type The type of entity to search for.
	 * @param range The range within which to search for the entity.
	 */
	protected BasicEntity findNearestEntity(Class<? extends BasicEntity> type, int range) {
		if (type == null) {
			return null;
		}
		BasicEntity nearest = null;
		int minDist = Integer.MAX_VALUE;
		for (int dx = -range; dx <= range; dx++) {
			for (int dy = -range; dy <= range; dy++) {
				
				int nx = x + dx, ny = y + dy;
				World.Tile tile = grid.getTile(nx, ny);
				
				if (tile == null) { continue; }

				for (BasicEntity other : tile.getEntities()) {
					if (other == this || !type.isInstance(other) || !other.isAlive()) { continue; }
					int dist = Math.abs(dx) + Math.abs(dy);

					if (dist < minDist) { minDist = dist; nearest = other; }
				}
			}
		}
		return nearest;
	}

	// Methods for managing the move counter
	public void incrementMoveCounter() { moveCounter++; }
	public boolean isTimeToMove() { return moveCounter >= getMovementSpeed(); }
	public void resetMoveCounter() { moveCounter = 0; }

	// Methods for managing the bazinga flag
	public void setBazinged() { hasBazinged = true; }
	public void resetBazinged() { hasBazinged = false; }
	public boolean hasBazinged() { return this.hasBazinged; }

	// enums for action type and direction
	public enum ActionType { MOVE, INTERACT }
	public enum Direction { UP, DOWN, LEFT, RIGHT }

	// Class for representing an action request
	public static class Request {
		public final ActionType type;
		public final Direction direction;
		public final int targetId;

		// Constructor for a move request
		public Request(ActionType type, Direction direction) {
			this.type = type;
			this.direction = direction;
			this.targetId = -1;
		}

		// Constructor for an interact request
		public Request(ActionType type, int targetId) {
			this.type = type;
			this.targetId = targetId;
			this.direction = null;
		}

		// Method that converts the request to a string (used in view)
		public String toActionString(BasicEntity self, Function<Integer, BasicEntity> entityLookup) {
			if (type == ActionType.MOVE) {
				if (direction == null) { return "IDLE"; }
				return "MOVE_" + direction.name();

			} else if (type == ActionType.INTERACT) {
				BasicEntity target = entityLookup.apply(targetId);
				if (target == null || !target.isAlive()) { return "IDLE"; }
				if (self.getPreyType() != null && self.getPreyType().isInstance(target)) { return "EAT"; }
				if (self.getClass().equals(target.getClass()) && self.getEnergy() >= self.getEnergyBazinga() && !self.hasBazinged && !target.hasBazinged()) { return "BAZINGA"; }

				return "IDLE";
			}
			return "IDLE";
		}
	}

	// Interface for representing the state of an entity
	public interface State { }
}
