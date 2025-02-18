package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;

/** A plant entity that does nothing on update. */
public class PlantEntity extends BasicEntity {

	private boolean dead = false;
	private int revivalTimer = 0;
	private static final int REVIVAL_TIME = 10;


	public PlantEntity(World grid, int x, int y) {
		super(grid, x, y);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public void update() {
		if (dead) {
			if (revivalTimer-- <= 0) {
				dead = false;
			}
		} 
	}

	@Override
	public void kill() {
		dead = true;
		revivalTimer = REVIVAL_TIME;
	}

	public boolean isAlive() {
		return !dead;
	}
}
