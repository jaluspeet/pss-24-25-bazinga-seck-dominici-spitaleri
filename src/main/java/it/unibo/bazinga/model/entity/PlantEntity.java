package it.unibo.bazinga.model.entity;

import it.unibo.bazinga.model.world.World;

/**
 * Represents a plant entity in the simulation.
 * Plants are immobile entities that can be eaten by sheep.
 * Plants have a resurrection delay, which is the number of turns that must pass
 * before a plant can be eaten again after being eaten.
 * Plants have a fixed energy value of 1.
 */
public class PlantEntity extends BasicEntity {
	private int resurrectionDelay = 0;

	/**
	 * Constructor for a plant.
	 *
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param initialEnergy the initial energy value
	 */
	public PlantEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
		this.zIndex = 1;
	}

	@Override
	protected State initialState() { return new PlantState(); }

	@Override
	public Request getNextRequest() { return null; }

	@Override
	public void transitionState(boolean actionSuccess) {}

	@Override
	public Class<? extends BasicEntity> getPreyType() { return null; }

	@Override
	public Class<? extends BasicEntity> getPredatorType() { return SheepEntity.class; }

	@Override
	public BasicEntity spawnOffspring() { return new PlantEntity(grid, x, y, 1); }

	@Override
	public boolean isAlive() { return energy > 0; }

	@Override
	public int getMovementSpeed() { return 0; }
	public void kill() { energy = 0; }
	public int getResurrectionDelay() { return resurrectionDelay; }
	public void setResurrectionDelay(int value) { this.resurrectionDelay = value; }
	public void decrementResurrectionDelay() { if (resurrectionDelay > 0) { resurrectionDelay--; } }

	private static class PlantState implements State {
	}
}
