package it.unibo.pss.controller.model;

import it.unibo.pss.model.world.WorldGrid;

/** DTO for transferring model data to the view. */
public class ModelDTO {
	private final WorldGrid grid;

	public ModelDTO(WorldGrid grid) {
		this.grid = grid;
	}

	/** Returns the world grid. */
	public WorldGrid getGrid() {
		return grid;
	}
}
