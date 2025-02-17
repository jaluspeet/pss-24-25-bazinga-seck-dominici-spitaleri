package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;

/** A plant entity that does nothing on update. */
public class PlantEntity extends BasicEntity {
	public PlantEntity(World grid, int x, int y) {
		super(grid, x, y);
	}
	
	@Override
	public void update() {
		// PlantEntity remains static.
	}
}
