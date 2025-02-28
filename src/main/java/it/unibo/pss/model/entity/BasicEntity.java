package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;

/** Base class for all entities */
public abstract class BasicEntity {
	protected final World grid;
	private final int id;
	protected int x, y;
	protected int energy;
	protected boolean hasBazinged;
	protected State currentState;
	private static int nextId = 0;

	private int stuckCounter = 0; // Tracks consecutive movement failures
	private static final int STUCK_THRESHOLD = 3; // Max failures before forced movement

	public BasicEntity(World grid, int x, int y, int initialEnergy) {
		this.grid = grid;
		this.id = nextId++;
		this.x = x;
		this.y = y;
		this.energy = initialEnergy;
		this.hasBazinged = false;
		this.currentState = initialState();
	}

	protected abstract State initialState();
	public abstract Request getNextRequest();
	public abstract void transitionState(boolean actionSuccess);

	public abstract Class<? extends BasicEntity> getPreyType();
	public abstract Class<? extends BasicEntity> getPredatorType();
	public abstract BasicEntity spawnOffspring();

	public int getId() { return id; }
	public int getX() { return x; }
	public int getY() { return y; }
	public int getEnergy() { return energy; }
	public void addEnergy(int amount) { energy += amount; }
	public void subtractEnergy(int amount) { energy -= amount; }
	public void setPosition(int newX, int newY) { x = newX; y = newY; }
	public void setBazinged() { hasBazinged = true; }
	public void resetBazinged() { hasBazinged = false; }

	public void resetStuckCounter() { stuckCounter = 0; }
	public void incrementStuckCounter() { stuckCounter++; }
	public boolean isStuck() { return stuckCounter >= STUCK_THRESHOLD; }

	public enum Direction { UP, DOWN, LEFT, RIGHT }
	public enum ActionType { MOVE, INTERACT }

	public boolean isAlive() {
		return energy > 0;
	}

	/** Finds the nearest entity of the given type within the specified range. */
	protected BasicEntity findNearestEntity(Class<? extends BasicEntity> type, int range) {
		BasicEntity nearest = null;
		int minDist = Integer.MAX_VALUE;
		for (int dx = -range; dx <= range; dx++) {
			for (int dy = -range; dy <= range; dy++) {
				if (dx == 0 && dy == 0) continue;
				int nx = x + dx, ny = y + dy;
				World.Tile tile = grid.getTile(nx, ny);
				if (tile == null) continue;
				for (BasicEntity other : tile.getEntities()) {
					if (type != null && type.isInstance(other) && other.isAlive() && other != this) {
						int dist = Math.abs(dx) + Math.abs(dy);
						if (dist < minDist) {
							minDist = dist;
							nearest = other;
						}
					}
				}
			}
		}
		return nearest;
	}

	/** Request class for actions */
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
	}

	public interface State { }
}
