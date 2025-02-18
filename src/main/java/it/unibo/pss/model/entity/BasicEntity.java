package it.unibo.pss.model.entity;

import java.util.List;
import it.unibo.pss.model.world.World;

/** Represents a basic entity in the simulation. */
public class BasicEntity {
	protected int x;
	protected int y;
	protected final World grid;
	private static int nextId = 0;
	private final int id;

	public BasicEntity(World grid, int x, int y) {
		this.grid = grid;
		this.x = x;
		this.y = y;
		this.id = nextId++;
		grid.getTile(x, y).addEntity(this);
	}

	/** Returns the tiles surrounding this entity within the given range. */
	public List<World.Tile> getSurroundingTiles(int range) {
		return grid.getTilesInRange(x, y, range);
	}

	/** Moves the entity to the given coordinates. */
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

	/** Default update method (does nothing). */
	public void update() { }

	public int getId() {
		return id;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
