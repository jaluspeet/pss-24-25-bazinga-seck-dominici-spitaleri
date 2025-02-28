package it.unibo.pss.model.entity.states;

import it.unibo.pss.model.entity.AnimalEntity;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.common.SharedConstants;

public class EatingState implements BasicEntity.EntityState {
	@Override
	public void execute(BasicEntity entity) {
		AnimalEntity a = (AnimalEntity) entity;
		BasicEntity targetFood = a.findNearestTarget(a.getFoodType());
		if (targetFood != null) {
			a.moveTowards(targetFood.getX(), targetFood.getY());
			if (a.getX() == targetFood.getX() && a.getY() == targetFood.getY()) {
				targetFood.kill();
				a.setEnergy(a.getEnergy() + SharedConstants.SHEEP_ENERGY_RESTORE);
			}
		} else {
			a.moveRandomly();
		}
		a.finalizeAction();
	}

	@Override
	public String getName() {
		return "EATING";
	}
}
