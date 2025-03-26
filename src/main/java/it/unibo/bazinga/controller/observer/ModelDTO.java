package it.unibo.bazinga.controller.observer;

import it.unibo.bazinga.model.world.World;
import java.util.Map;

/**
 * Data Transfer Object that contains the model data to be sent to the view.
 * Contains the grid and the actions of the entities.
 */
public class ModelDTO {
	private final World grid;
	private final Map<Integer,String> entityActions;

	/**
	 * Constructor.
	 *
	 * @param grid the grid.
	 * @param entityActions the actions of the entities.
	 */
	public ModelDTO(World grid, Map<Integer,String> entityActions) {
		this.grid = grid;
		this.entityActions = entityActions;
	}

	public World getGrid() { return grid; }
	public Map<Integer,String> getEntityActions() { return entityActions; }
}
