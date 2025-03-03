package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

public class WolfEntity extends BasicEntity {
	public WolfEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
	}

	@Override
	protected State initialState() {
		return new WolfState();
	}

	@Override
	public Request getNextRequest() {
		// Hunt
		BasicEntity prey = findNearestEntity(getPreyType(), SharedConstants.WOLF_SIGHT_RANGE);
		if (prey != null) return moveOrInteract(prey);

		// Mate
		if (energy >= SharedConstants.WOLF_ENERGY_BAZINGA) {
			BasicEntity mate = findNearestEntity(this.getClass(), SharedConstants.WOLF_SIGHT_RANGE);
			if (mate != null) return moveOrInteract(mate);
		}

		// Move randomly
		return new Request(ActionType.MOVE, randomDirection());
	}

	@Override
	public void transitionState(boolean actionSuccess) {}

	@Override
	public Class<? extends BasicEntity> getPreyType() { return SheepEntity.class; }
	@Override
	public Class<? extends BasicEntity> getPredatorType() { return null; }
	@Override
	public int getMovementSpeed() { return SharedConstants.WOLF_MOVEMENT_SPEED; }
	@Override
	public BasicEntity spawnOffspring() { return new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT); }

	private static class WolfState implements State {}

	private Request moveOrInteract(BasicEntity target) {
		int dist = Math.abs(x - target.getX()) + Math.abs(y - target.getY());
		return (dist <= 1) ? new Request(ActionType.INTERACT, target.getId()) : moveToward(target);
	}

	private Request moveToward(BasicEntity target) {
		int dx = target.getX() - x, dy = target.getY() - y;
		Direction moveDir = (Math.abs(dx) >= Math.abs(dy)) ? (dx > 0 ? Direction.RIGHT : Direction.LEFT) : (dy > 0 ? Direction.DOWN : Direction.UP);
		return new Request(ActionType.MOVE, moveDir);
	}

	private Direction randomDirection() {
		Direction[] dirs = Direction.values();
		return dirs[(int)(Math.random() * dirs.length)];
	}
}
