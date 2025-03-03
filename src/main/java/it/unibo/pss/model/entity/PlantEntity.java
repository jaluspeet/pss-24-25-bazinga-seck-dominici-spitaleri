package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

public class PlantEntity extends BasicEntity {
	private int resurrectionDelay = 0;

	public PlantEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
	}

	@Override
	protected State initialState() { return new PlantState(); }
	@Override
	public Request getNextRequest() { return null; }
	@Override
	public void transitionState(boolean actionSuccess) {}
	@Override
	public Class<? extends BasicEntity> getPreyType() { return null; }
	@Override
	public Class<? extends BasicEntity> getPredatorType() { return SheepEntity.class; }
	@Override
	public BasicEntity spawnOffspring() { return new PlantEntity(grid, x, y, 1); }

	public void kill() {
		energy = 0;
		resurrectionDelay = SharedConstants.PLANT_RESURRECTION_TIME;
	}

	@Override
	public boolean isAlive() { return energy > 0; }
	public int getResurrectionDelay() { return resurrectionDelay; }
	public void decrementResurrectionDelay() { if (resurrectionDelay > 0) resurrectionDelay--; }
	@Override
	public int getMovementSpeed() { return 0; }

	private static class PlantState implements State {}
}
