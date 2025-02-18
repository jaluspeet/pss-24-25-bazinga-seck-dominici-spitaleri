package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

public class PreyEntity extends AnimalEntity {

	/* Constructor for PreyEntity. */
	public PreyEntity(World grid, int x, int y) {
		super(grid, x, y, SharedConstants.PREY_SPEED);
	}

	/* check for nearby predators, and switch to FleeingState if one is detected. */
	@Override
	protected void updateState() {
		BasicEntity predatorCandidate = findNearestTarget(PredatorEntity.class);
		AnimalEntity predator = (predatorCandidate instanceof AnimalEntity)
			? (AnimalEntity) predatorCandidate : null;
		if (predator != null && !(state instanceof it.unibo.pss.model.entity.states.FleeingState)) {
			setState(new it.unibo.pss.model.entity.states.FleeingState(predator));
		} else {
			super.updateState();
		}
	}

	/* spawn a new prey */
	@Override
	public void spawnOffspring() {
		new PreyEntity(grid, this.x, this.y);
	}

	/* return the prey's food type */
	@Override
	public Class<? extends BasicEntity> getFoodType() {
		return PlantEntity.class;
	}

	/* return the prey's mate type */
	@Override
	public Class<? extends BasicEntity> getMateType() {
		return PreyEntity.class;
	}
}
