package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

public class PredatorEntity extends AnimalEntity {

	/**
	 * Constructs a PredatorEntity.
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public PredatorEntity(World grid, int x, int y) {
		super(grid, x, y, SharedConstants.PREDATOR_SPEED);
	}

	@Override
	protected Class<? extends BasicEntity> getFoodType() {
		return PreyEntity.class;
	}

	@Override
	protected Class<? extends BasicEntity> getMateType() {
		return PredatorEntity.class;
	}

	@Override
	protected void spawnOffspring() {
		new PredatorEntity(grid, this.x, this.y);
	}
}
