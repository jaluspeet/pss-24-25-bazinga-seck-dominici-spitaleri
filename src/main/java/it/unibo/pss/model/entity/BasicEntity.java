package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.WorldGrid;

/** A basic concrete entity. */
public class BasicEntity extends Entity {
	public BasicEntity(WorldGrid grid, int x, int y) {
		super(grid, x, y);
	}
}
