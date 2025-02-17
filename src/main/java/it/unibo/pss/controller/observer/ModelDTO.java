package it.unibo.pss.controller.observer;

import it.unibo.pss.model.world.World;

/** DTO for transferring model data to the view. */
public class ModelDTO {
	private final World grid;

	public ModelDTO(World grid) {
		this.grid = grid;
	}

	/** Returns the world grid. */
	public World getGrid() {
		return grid;
	}
}
