package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

public class SheepEntity extends BasicEntity {
	private int failCount;

	public SheepEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
		setSightRange(SharedConstants.SHEEP_SIGHT_RANGE);
		setEnergyBazinga(SharedConstants.SHEEP_ENERGY_BAZINGA);
		setEnergyHungry(SharedConstants.SHEEP_ENERGY_HUNGRY);
		setEnergyRestore(SharedConstants.SHEEP_ENERGY_RESTORE);
		setMovementSpeed(SharedConstants.SHEEP_MOVEMENT_SPEED);
	        this.zIndex = 0;
	}

	@Override
	public Request getNextRequest() {
		if (failCount >= 2) {
			failCount = 0;
			return new Request(ActionType.MOVE, randomDirection());
		}

		int fleeRange = Math.max(1, getSightRange());
		BasicEntity predator = findNearestEntity(getPredatorType(), fleeRange);
		if (predator != null) {
			return moveAway(predator);
		}

		if (getEnergy() < getEnergyHungry()) {
			BasicEntity prey = findNearestEntity(getPreyType(), getSightRange());
			if (prey != null && prey.isAlive()) {
				return moveOrInteract(prey);
			}
		}

		if (getEnergy() >= getEnergyBazinga()) {
			BasicEntity mate = findNearestEntity(this.getClass(), getSightRange());
			if (mate != null) {
				return moveOrInteract(mate);
			}
		}

		return new Request(ActionType.MOVE, randomDirection());
	}

	@Override
	protected State initialState() {
		return new SheepState();
	}

	@Override
	public Class<? extends BasicEntity> getPreyType() {
		return PlantEntity.class;
	}

	@Override
	public Class<? extends BasicEntity> getPredatorType() {
		return WolfEntity.class;
	}

	@Override
	public BasicEntity spawnOffspring() {
		return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT);
	}

	@Override
	public void transitionState(boolean actionSuccess) {
		if (actionSuccess) {
			failCount = 0;
		} else {
			failCount++;
		}
	}

	private static class SheepState implements State {
	}
}
