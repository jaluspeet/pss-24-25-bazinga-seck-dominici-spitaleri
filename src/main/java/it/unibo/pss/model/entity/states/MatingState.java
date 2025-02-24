package it.unibo.pss.model.entity.states;

import it.unibo.pss.model.entity.AnimalEntity;
import it.unibo.pss.model.entity.BasicEntity;

public class MatingState implements BasicEntity.EntityState {
	@Override
	public void execute(BasicEntity entity) {
		AnimalEntity a = (AnimalEntity) entity;
		BasicEntity mateEntity = a.findNearestTarget(a.getMateType());
		if (mateEntity != null && mateEntity != a) {
			int distance = Math.abs(a.getX() - mateEntity.getX()) + Math.abs(a.getY() - mateEntity.getY());
			if (distance > 1) {
				a.moveTowards(mateEntity.getX(), mateEntity.getY());
			} else {
				AnimalEntity mate = (AnimalEntity) mateEntity;
				a.performMating(mate);
			}
		} else {
			a.moveRandomly();
		}
		a.finalizeAction();
	}

	@Override
	public String getName() {
		return "MATING";
	}
}
