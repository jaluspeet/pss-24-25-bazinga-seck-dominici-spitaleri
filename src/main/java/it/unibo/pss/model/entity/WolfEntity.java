package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

public class WolfEntity extends BasicEntity {
	public WolfEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
		setSightRange(SharedConstants.WOLF_SIGHT_RANGE);
		setEnergyBazinga(SharedConstants.WOLF_ENERGY_BAZINGA);
		setEnergyHungry(SharedConstants.WOLF_ENERGY_HUNGRY);
		setEnergyRestore(SharedConstants.WOLF_ENERGY_RESTORE);
		setMovementSpeed(SharedConstants.WOLF_MOVEMENT_SPEED);
	}

	@Override
	public Request getNextRequest() {
		// hunt
		BasicEntity prey = findNearestEntity(getPreyType(), this.sightRange);
		if (prey != null) return moveOrInteract(prey);

		// bazinga
		if (energy >= energyBazinga) {
			BasicEntity mate = findNearestEntity(this.getClass(), this.sightRange);
			if (mate != null) return moveOrInteract(mate);
		}

		// move randomly
		return new Request(ActionType.MOVE, randomDirection());
	}

	@Override
	protected State initialState() { return new WolfState(); }
	@Override
	public Class<? extends BasicEntity> getPreyType() { return SheepEntity.class; }
	@Override
	public Class<? extends BasicEntity> getPredatorType() { return null; }
	@Override
	public BasicEntity spawnOffspring() { return new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT); }
	@Override
	public void transitionState(boolean actionSuccess) {}

	private static class WolfState implements State {}
}
