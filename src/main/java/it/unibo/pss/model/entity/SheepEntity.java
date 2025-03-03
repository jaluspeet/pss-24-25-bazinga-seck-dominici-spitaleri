package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

public class SheepEntity extends BasicEntity {
	public SheepEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
	}

	@Override
	protected State initialState() {
		return new SheepState();
	}

	@Override
	public Request getNextRequest() {
		// Flee
		BasicEntity predator = findNearestEntity(getPredatorType(), SharedConstants.SHEEP_SIGHT_RANGE);
		if (predator != null) return moveAway(predator);

		// Eat
		if (energy < SharedConstants.SHEEP_ENERGY_HUNGRY) {
			BasicEntity prey = findNearestEntity(getPreyType(), SharedConstants.SHEEP_SIGHT_RANGE);
			if (prey != null && prey.isAlive()) return moveOrInteract(prey);
		}

		// Mate
		if (energy >= SharedConstants.SHEEP_ENERGY_BAZINGA) {
			BasicEntity mate = findNearestEntity(this.getClass(), SharedConstants.SHEEP_SIGHT_RANGE);
			if (mate != null) return moveOrInteract(mate);
		}

		// Move randomly
		return new Request(ActionType.MOVE, randomDirection());
	}

	@Override
	public void transitionState(boolean actionSuccess) {}

	@Override
	public Class<? extends BasicEntity> getPreyType() { return PlantEntity.class; }
	@Override
	public Class<? extends BasicEntity> getPredatorType() { return WolfEntity.class; }
	@Override
	public int getMovementSpeed() { return SharedConstants.SHEEP_MOVEMENT_SPEED; }
	@Override
	public BasicEntity spawnOffspring() { return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT); }

	private static class SheepState implements State {}

	private Request moveAway(BasicEntity entity) {
		Direction bestDirection = null;
		int maxDist = Math.abs(x - entity.getX()) + Math.abs(y - entity.getY());

		for (Direction dir : Direction.values()) {
			int newX = x, newY = y;
			switch (dir) {
				case UP:    newY--; break;
				case DOWN:  newY++; break;
				case LEFT:  newX--; break;
				case RIGHT: newX++; break;
			}

			int newDist = Math.abs(newX - entity.getX()) + Math.abs(newY - entity.getY());
			if (newDist > maxDist) {
				maxDist = newDist;
				bestDirection = dir;
			}
		}

		// If no better move was found, move randomly
		return new Request(ActionType.MOVE, (bestDirection != null) ? bestDirection : randomDirection());
	}

	private Request moveOrInteract(BasicEntity target) {
		int dist = Math.abs(x - target.getX()) + Math.abs(y - target.getY());
		return (dist <= 1) ? new Request(ActionType.INTERACT, target.getId()) : moveToward(target);
	}

	/**
	 * Moves towards the target entity.
	 */
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
