package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

/** A predator entity that uses PreyEntity as food and reproduces with other PredatorEntity instances. */
public class PredatorEntity extends AnimalEntity {

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
