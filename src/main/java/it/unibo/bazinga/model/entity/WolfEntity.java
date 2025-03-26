package it.unibo.bazinga.model.entity;

import it.unibo.bazinga.common.SharedConstants;
import it.unibo.bazinga.model.world.World;

/**
 * The WolfEntity class represents a wolf entity in the simulation.
 */
public class WolfEntity extends BasicEntity {
	private int failCount;

	/**
	 * Constructor for WolfEntity
	 *
	 * @param grid          the world grid
	 * @param x             the x coordinate
	 * @param y             the y coordinate
	 * @param initialEnergy the initial energy
	 */
	public WolfEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
		setSightRange(SharedConstants.WOLF_SIGHT_RANGE);
		setEnergyBazinga(SharedConstants.WOLF_ENERGY_BAZINGA);
		setEnergyHungry(SharedConstants.WOLF_ENERGY_HUNGRY);
		setEnergyRestore(SharedConstants.WOLF_ENERGY_RESTORE);
		setMovementSpeed(SharedConstants.WOLF_MOVEMENT_SPEED);
		this.zIndex = 2;
	}

	/**
	 * initializes the state
	 */
	@Override
	protected State initialState() { return new WolfState(); }

	/**
	 * Returns the next request for the wolf entity, effectively implementing the wolf's behavior.
	 */
	@Override
	public Request getNextRequest() {

		// If the wolf fails to move twice in a row, it will try to move in a random direction
		if (failCount >= 2) {
			failCount = 0;
			return new Request(ActionType.MOVE, randomDirection());
		}

		// If the wolf is hungry, it will try to find and eat a sheep
		BasicEntity prey = findNearestEntity(getPreyType(), getSightRange());
		if (prey != null) { return moveOrInteract(prey); }

		// If the wolf has enough energy, it will try to find a mate
		if (getEnergy() >= getEnergyBazinga()) {
			BasicEntity mate = findNearestEntity(this.getClass(), getSightRange());
			if (mate != null) { return moveOrInteract(mate); }
		}

		return new Request(ActionType.MOVE, randomDirection());
	}

	/**
	 * Transitions the state of the wolf entity based on the success of the last action.
	 */
	@Override
	public void transitionState(boolean actionSuccess) {
		if (actionSuccess) { failCount = 0;
		} else { failCount++; }
	}

	@Override
	public Class<? extends BasicEntity> getPreyType() { return SheepEntity.class; }

	@Override
	public Class<? extends BasicEntity> getPredatorType() { return null; }

	@Override
	public BasicEntity spawnOffspring() { return new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT); }

	private static class WolfState implements State {}
}
