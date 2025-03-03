package it.unibo.pss.controller.observer;

import it.unibo.pss.model.world.World;
import java.util.Map;

public class ModelDTO {
	private final World grid;
	private final Map<Integer,String> entityActions;

	public ModelDTO(World grid, Map<Integer,String> entityActions) {
		this.grid = grid;
		this.entityActions = entityActions;
	}

	public World getGrid() {
		return grid;
	}

	public Map<Integer,String> getEntityActions() {
		return entityActions;
	}
}
