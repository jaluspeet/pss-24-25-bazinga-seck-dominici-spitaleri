package it.unibo.pss.model.entity;

import it.unibo.pss.model.entity.states.EatingState;
import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

public class PredatorEntity extends AnimalEntity {

	/* Constructor for PredatorEntity. */
	public PredatorEntity(World grid, int x, int y) {
		super(grid, x, y, SharedConstants.PREDATOR_SPEED);
	}

	/* spawn a new predator */
	@Override
	public void spawnOffspring() {
		new PredatorEntity(grid, this.x, this.y);
	}

	/* return the predator's energy */
	@Override
	public Class<? extends BasicEntity> getFoodType() {
		return PreyEntity.class;
	}

	/* return the predator's mate type */
	@Override
	public Class<? extends BasicEntity> getMateType() {
		return PredatorEntity.class;
	}

	@Override
	public int getSeekRadius() {
		return SharedConstants.PREDATOR_SEEK_RADIUS;
	}

	@Override
	protected void updateState() {
		super.updateState();
		if (!isAlive())
			return;
		BasicEntity preyCandidate = findNearestTarget(PreyEntity.class);
		if (preyCandidate != null) {
			setState(new EatingState());
		} else {
			setState(defaultState());
		}
	}
}
