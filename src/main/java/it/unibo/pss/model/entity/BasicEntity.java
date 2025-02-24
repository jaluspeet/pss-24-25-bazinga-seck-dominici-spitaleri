package it.unibo.pss.model.entity;

import java.util.List;
import it.unibo.pss.model.world.World;

public class BasicEntity {
	protected int x;
	protected int y;
	protected final World grid;
	private static int nextId = 0;
	private final int id;
	private static final int ENTITY_STATE_TIME = 5;

	protected EntityState state;
	protected int stateLock;

	/** Constructor for BasicEntity. */
	public BasicEntity(World grid, int x, int y) {
		this.grid = grid;
		this.x = x;
		this.y = y;
		this.id = nextId++;
		grid.getTile(x, y).addEntity(this);
		this.state = new IdleState();
		this.stateLock = 0;
	}

	/** Updates the entity state and behavior. */
	public final void update() {
		updateState();
		if (stateLock > 0) {
			stateLock--;
		} else if (!(state instanceof IdleState) && !(state instanceof DeadState)) {
			setState(new IdleState());
		}
		state.execute(this);
	}

	/** Override this method to set the default behavior for the entity. */
	protected void updateState() { }

	/** Sets the entity state. */
	public void setState(EntityState newState) {
		this.state = newState;
		this.stateLock = ENTITY_STATE_TIME;
	}


	/** Moves the entity to the new position. */
	public void moveTo(int newX, int newY) {
		grid.getTile(x, y).removeEntity(this);
		this.x = newX;
		this.y = newY;
		grid.getTile(newX, newY).addEntity(this);
	}

	/** Kills the entity. */
	public void kill() { }

	/** Returns true if the entity is alive. */
	public boolean isAlive() {
		return true;
	}

	/** Returns the surrounding tiles. */
	public List<World.Tile> getSurroundingTiles(int range) {
		return grid.getTilesInRange(x, y, range);
	}

	/** Returns the entity's ID. */
	public int getId() {
		return id;
	}

	/** Returns the entity's X position. */
	public int getX() {
		return x;
	}

	/** Returns the entity's Y position. */
	public int getY() {
		return y;
	}

	/** Returns the entity's grid. */
	public World getGrid() {
		return grid;
	}

	/** Base state interface. */
	public interface EntityState {
		void execute(BasicEntity entity);
		String getName();
	}

	/** IDLE state. */
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
