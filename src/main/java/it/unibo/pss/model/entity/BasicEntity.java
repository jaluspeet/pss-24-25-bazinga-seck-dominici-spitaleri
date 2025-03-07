package it.unibo.pss.model.entity;

import java.util.function.Function;

import it.unibo.pss.model.world.World;

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

	protected abstract State initialState();
	public abstract Request getNextRequest();
	public abstract void transitionState(boolean actionSuccess);
	public abstract Class<? extends BasicEntity> getPreyType();
	public abstract Class<? extends BasicEntity> getPredatorType();
	public abstract BasicEntity spawnOffspring();

	public int getSightRange() { return sightRange; }
	public void setSightRange(int sightRange) { this.sightRange = sightRange; }
	public int getEnergyBazinga() { return energyBazinga; }
	public void setEnergyBazinga(int energyBazinga) { this.energyBazinga = energyBazinga; }
	public int getEnergyHungry() { return energyHungry; }
	public void setEnergyHungry(int energyHungry) { this.energyHungry = energyHungry; }
	public int getEnergyRestore() { return energyRestore; }
	public void setEnergyRestore(int energyRestore) { this.energyRestore = energyRestore; }
	public int getMovementSpeed() { return movementSpeed; }
	public void setMovementSpeed(int movementSpeed) { this.movementSpeed = movementSpeed; }

	public int getZIndex() { return zIndex; }
	public int getId() { return id; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getEnergy() { return energy; }

	public void addEnergy(int amount) { energy += amount; }
	public void subtractEnergy(int amount) { energy = Math.max(0, energy - amount); }
	public void setPosition(int newX, int newY) { x = newX; y = newY; }

	public void setBazinged() { hasBazinged = true; }
	public void resetBazinged() { hasBazinged = false; }
	public boolean hasBazinged() { return this.hasBazinged; }

	public boolean isAlive() { return energy > 0; }
	public void incrementMoveCounter() { moveCounter++; }
	public boolean isTimeToMove() { return moveCounter >= getMovementSpeed(); }
	public void resetMoveCounter() { moveCounter = 0; }
	protected Request moveOrInteract(BasicEntity target) {
		int dist = Math.abs(x - target.getX()) + Math.abs(y - target.getY());
		if (dist <= 1) {
			return new Request(ActionType.INTERACT, target.getId());
		} else {
			return moveToward(target);
		}
	}

	protected Request moveToward(BasicEntity target) {
		int dx = target.getX() - x;
		int dy = target.getY() - y;
		Direction moveDir = (Math.abs(dx) >= Math.abs(dy))
			? (dx > 0 ? Direction.RIGHT : Direction.LEFT)
			: (dy > 0 ? Direction.DOWN : Direction.UP);
		return new Request(ActionType.MOVE, moveDir);
	}

	protected Request moveAway(BasicEntity entity) {
		Direction bestDirection = null;
		int maxDist = Math.abs(x - entity.getX()) + Math.abs(y - entity.getY());
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

	protected Direction randomDirection() {
		Direction[] dirs = Direction.values();
		return dirs[(int) (Math.random() * dirs.length)];
	}

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
				if (tile == null) {
					continue;
				}
				for (BasicEntity other : tile.getEntities()) {
					if (other == this || !type.isInstance(other) || !other.isAlive()) {
						continue;
					}
					int dist = Math.abs(dx) + Math.abs(dy);
					if (dist < minDist) {
						minDist = dist;
						nearest = other;
					}
				}
			}
		}
		return nearest;
	}

	public enum ActionType { MOVE, INTERACT }

	public enum Direction { UP, DOWN, LEFT, RIGHT }

	public static class Request {
		public final ActionType type;
		public final Direction direction;
		public final int targetId;

		public Request(ActionType type, Direction direction) {
			this.type = type;
			this.direction = direction;
			this.targetId = -1;
		}

		public Request(ActionType type, int targetId) {
			this.type = type;
			this.targetId = targetId;
			this.direction = null;
		}

		public String toActionString(BasicEntity self, Function<Integer, BasicEntity> entityLookup) {
			if (type == ActionType.MOVE) {
				if (direction == null) {
					return "IDLE";
				}
				return "MOVE_" + direction.name();
			} else if (type == ActionType.INTERACT) {
				BasicEntity target = entityLookup.apply(targetId);
				if (target == null || !target.isAlive()) {
					return "IDLE";
				}
				if (self.getPreyType() != null && self.getPreyType().isInstance(target)) {
					return "EAT";
				}
				if (self.getClass().equals(target.getClass())
						&& self.getEnergy() >= self.getEnergyBazinga()
						&& !self.hasBazinged
						&& !target.hasBazinged()) {
					return "BAZINGA";
						}
				return "IDLE";
			}
			return "IDLE";
		}
	}

	public interface State { }
}
