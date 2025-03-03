package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

public class SheepEntity extends BasicEntity {
	public SheepEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
		setSightRange(SharedConstants.SHEEP_SIGHT_RANGE);
		setEnergyBazinga(SharedConstants.SHEEP_ENERGY_BAZINGA);
		setEnergyHungry(SharedConstants.SHEEP_ENERGY_HUNGRY);
		setEnergyRestore(SharedConstants.SHEEP_ENERGY_RESTORE);
		setMovementSpeed(SharedConstants.SHEEP_MOVEMENT_SPEED);
	}

	@Override
	public Request getNextRequest() {
		// flee
		BasicEntity predator = findNearestEntity(getPredatorType(), this.sightRange);
		if (predator != null) return moveAway(predator);

		// eat
		if (energy < energyHungry) {
			BasicEntity prey = findNearestEntity(getPreyType(), this.sightRange);
			if (prey != null && prey.isAlive()) return moveOrInteract(prey);
		}

		// bazinga
		if (energy >= energyBazinga) {
			BasicEntity mate = findNearestEntity(this.getClass(), this.sightRange);
			if (mate != null) return moveOrInteract(mate);
		}

		// move randomly
		return new Request(ActionType.MOVE, randomDirection());
	}

	@Override
	protected State initialState() { return new SheepState(); }
	@Override
	public Class<? extends BasicEntity> getPreyType() { return PlantEntity.class; }
	@Override
	public Class<? extends BasicEntity> getPredatorType() { return WolfEntity.class; }
	@Override
	public BasicEntity spawnOffspring() { return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT); }
	@Override
	public void transitionState(boolean actionSuccess) {}

	private static class SheepState implements State {}
}
