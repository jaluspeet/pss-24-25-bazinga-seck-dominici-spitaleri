package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

/** Plant entity */
public class PlantEntity extends BasicEntity {
	private int resurrectionCounter = 0;
	
	public PlantEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
	}
	
	@Override
	protected State initialState() {
		return new PlantState();
	}
	
	@Override
	public Request getNextRequest() {
		// Plants simply interact with themselves.
		return new Request(ActionType.INTERACT, this.getId());
	}
	
	@Override
	public void transitionState(boolean actionSuccess) {
		// No state transition needed for plants.
	}
	
	@Override
	public Class<? extends BasicEntity> getPreyType() {
		return null;
	}
	
	@Override
	public Class<? extends BasicEntity> getPredatorType() {
		return SheepEntity.class;
	}
	
	@Override
	public BasicEntity spawnOffspring() {
		return new PlantEntity(grid, x, y, 1);
	}
	
	public void kill() {
		energy = 0;
		resurrectionCounter = SharedConstants.PLANT_RESURRECTION_TIME;
	}
	
	public boolean isDead() {
		return energy <= 0;
	}
	
	public int getResurrectionCounter() {
		return resurrectionCounter;
	}
	
	public void decrementResurrectionCounter() {
		if (resurrectionCounter > 0)
			resurrectionCounter--;
	}
	
	private static class PlantState implements State { }
}
