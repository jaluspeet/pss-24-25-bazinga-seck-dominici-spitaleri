package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;

public class PlantEntity extends BasicEntity {
	private int revivalTimer = 0;
	private static final int REVIVAL_TIME = 10;

	/* Constructor for PlantEntity. */
	public PlantEntity(World grid, int x, int y) {
		super(grid, x, y);
	}


	/* Override updateState() for resurrection */
	@Override
	protected void updateState() {
		if (state instanceof DeadState) {
			if (revivalTimer-- <= 0) {
				setState(new IdleState());
			}
		}
	}

	/* Override kill() to set the entity to dead and start the revival timer */
	@Override
	public void kill() {
		revivalTimer = REVIVAL_TIME;
		setState(new DeadState());
	}

	/* check if the entity is alive */
	@Override
	public boolean isAlive() {
		return !(state instanceof DeadState);
	}
}
