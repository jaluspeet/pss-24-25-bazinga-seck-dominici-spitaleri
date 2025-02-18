package it.unibo.pss.model.entity.states;

import it.unibo.pss.model.entity.AnimalEntity;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.common.SharedConstants;

public class MatingState implements BasicEntity.EntityState {
	@Override
	public void execute(BasicEntity entity) {
		AnimalEntity a = (AnimalEntity) entity;
		BasicEntity mateEntity = a.findNearestTarget(a.getMateType());
		if (mateEntity != null && mateEntity != a) {
			a.moveTowards(mateEntity.getX(), mateEntity.getY());
			if (a.getX() == mateEntity.getX() && a.getY() == mateEntity.getY()) {
				AnimalEntity mate = (AnimalEntity) mateEntity;
				if (a.getEnergy() >= SharedConstants.ANIMAL_REPRODUCTION_COST &&
					mate.getEnergy() >= SharedConstants.ANIMAL_REPRODUCTION_COST &&
					a.getId() < mate.getId()) {
					a.setEnergy(a.getEnergy() - SharedConstants.ANIMAL_REPRODUCTION_COST);
					mate.setEnergy(mate.getEnergy() - SharedConstants.ANIMAL_REPRODUCTION_COST);
					a.spawnOffspring();
					mate.spawnOffspring();
				}
			}
		} else {
			a.moveRandomly();
		}
		a.setState(new BasicEntity.IdleState());
	}

	@Override
	public String getName() {
		return "MATING";
	}
}
