package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;

public class PlantEntity extends BasicEntity {
	private int revivalTimer = 0;
	private static final int REVIVAL_TIME = 10;

	/**
	 * Constructs a PlantEntity.
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public PlantEntity(World grid, int x, int y) {
		super(grid, x, y);
	}

	@Override
	public void update() {
		if(state instanceof DeadState) {
			if(revivalTimer-- <= 0) {
				setState(new IdleState());
			}
		}
		super.update();
	}

	@Override
	public void kill() {
		revivalTimer = REVIVAL_TIME;
		setState(new DeadState());
	}

	@Override
	public boolean isAlive() {
		return !(state instanceof DeadState);
	}
}
