package it.unibo.pss.model.entity;

import java.util.List;
import it.unibo.pss.model.world.World;

public class BasicEntity {
	protected int x;
	protected int y;
	protected final World grid;
	private static int nextId = 0;
	private final int id;
	private static final int ENTITY_STATE_TIME = 3;

	protected EntityState state;
	protected int stateLock;

	/**
	 * Constructs a BasicEntity.
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public BasicEntity(World grid, int x, int y) {
		this.grid = grid;
		this.x = x;
		this.y = y;
		this.id = nextId++;
		grid.getTile(x, y).addEntity(this);
		this.state = new IdleState();
		this.stateLock = 0;
	}

	/**
	 * Updates the entity. When not IDLE, the state is locked for ENTITY_STATE_TIME updates.
	 */
	public void update() {
		if(stateLock > 0) {
			stateLock--;
		} else if(!(state instanceof IdleState) && !(state instanceof DeadState)) {
			setState(new IdleState());
		}
		state.execute(this);
	}

	/**
	 * Changes the entity state and locks it for SharedConstants.ENTITY_STATE_TIME updates.
	 * @param newState the new state
	 */
	public void setState(EntityState newState) {
		this.state = newState;
		this.stateLock = ENTITY_STATE_TIME;
	}

	/**
	 * Moves the entity to the given coordinates.
	 * @param newX the new x coordinate
	 * @param newY the new y coordinate
	 */
	public void moveTo(int newX, int newY) {
		grid.getTile(x, y).removeEntity(this);
		this.x = newX;
		this.y = newY;
		grid.getTile(newX, newY).addEntity(this);
	}

	public void kill() { }

	public boolean isAlive() {
		return true;
	}

	/** Returns the surrounding tiles. */
	public List<World.Tile> getSurroundingTiles(int range) {
		return grid.getTilesInRange(x, y, range);
	}

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	/** Base state interface. */
	public interface EntityState {
		void execute(BasicEntity entity);
		String getName();
	}

	/** default IDLE state. */
	public static class IdleState implements EntityState {
		@Override
		public void execute(BasicEntity entity) { }
		@Override
		public String getName() {
			return "IDLE";
		}
	}

	/** DEAD state. Subclasses (like PlantEntity) may override kill behavior. */
	public static class DeadState implements EntityState {
		@Override
		public void execute(BasicEntity entity) { }
		@Override
		public String getName() {
			return "DEAD";
		}
	}
}
