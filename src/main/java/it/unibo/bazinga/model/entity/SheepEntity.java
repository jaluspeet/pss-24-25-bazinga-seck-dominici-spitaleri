package it.unibo.bazinga.model.entity;

import it.unibo.bazinga.common.SharedConstants;
import it.unibo.bazinga.model.world.World;

/**
 * A sheep entity that can move around, eat plants, mate with other sheep, and flee from wolves.
 */
public class SheepEntity extends BasicEntity {
	private int failCount;

	/**
	 * Constructor for sheep entity
	 *
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param initialEnergy the initial energy
	 */
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
	protected State initialState() { return new SheepState(); }


	/**
	 * Returns the next request for the sheep entity, effectively implementing the sheep's behavior.
	 *
	 * @return the next request
	 */
	@Override
	public Request getNextRequest() {

		// If the sheep has failed to move twice in a row, it will try a random direction
		if (failCount >= 2) {
			failCount = 0;
			return new Request(ActionType.MOVE, randomDirection());
		}

		// If there is a predator in sight, the sheep will try to flee
		int fleeRange = Math.max(1, getSightRange());
		BasicEntity predator = findNearestEntity(getPredatorType(), fleeRange);
		if (predator != null) { return moveAway(predator); }

		// If the sheep is hungry, it will try to find and eat plants
		if (getEnergy() < getEnergyHungry()) {
			BasicEntity prey = findNearestEntity(getPreyType(), getSightRange());
			if (prey != null && prey.isAlive()) { return moveOrInteract(prey); }
		}

		// If the sheep has enough energy, it will try to find a mate
		if (getEnergy() >= getEnergyBazinga()) {
			BasicEntity mate = findNearestEntity(this.getClass(), getSightRange());
			if (mate != null) { return moveOrInteract(mate); }
		}

		return new Request(ActionType.MOVE, randomDirection());
	}

	/** 
	 * Transitions the sheep entity's state based on the success of the last action.
	 */
	@Override
	public void transitionState(boolean actionSuccess) {
		if (actionSuccess) { failCount = 0; }
		else { failCount++; }
	}

	@Override
	public Class<? extends BasicEntity> getPreyType() { return PlantEntity.class; }

	@Override
	public Class<? extends BasicEntity> getPredatorType() { return WolfEntity.class; }

	@Override
	public BasicEntity spawnOffspring() { return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT); }

	private static class SheepState implements State {}
}
