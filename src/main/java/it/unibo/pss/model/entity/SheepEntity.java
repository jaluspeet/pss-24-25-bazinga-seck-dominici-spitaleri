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
		// flee
		BasicEntity predator = findNearestEntity(getPredatorType(), SharedConstants.SHEEP_SIGHT_RANGE);
		if (predator != null) {
			int currentDist = Math.abs(x - predator.getX()) + Math.abs(y - predator.getY());
			for (Direction dir : Direction.values()) {
				int newX = x, newY = y;
				switch (dir) {
					case UP:    newY--; break;
					case DOWN:  newY++; break;
					case LEFT:  newX--; break;
					case RIGHT: newX++; break;
				}
				int newDist = Math.abs(newX - predator.getX()) + Math.abs(newY - predator.getY());
				if (newDist > currentDist)
					return new Request(ActionType.MOVE, dir);
			}
		}

		// bazinga
		if (energy >= SharedConstants.SHEEP_ENERGY_BAZINGA) {
			BasicEntity mate = findNearestEntity(this.getClass(), SharedConstants.SHEEP_SIGHT_RANGE);
			if (mate != null && mate != this) {
				int dist = Math.abs(x - mate.getX()) + Math.abs(y - mate.getY());
				if (dist <= 1)
					return new Request(ActionType.INTERACT, mate.getId());
				else {
					int dx = mate.getX() - x;
					int dy = mate.getY() - y;
					Direction moveDir = (Math.abs(dx) >= Math.abs(dy))
						? ((dx > 0) ? Direction.RIGHT : Direction.LEFT)
						: ((dy > 0) ? Direction.DOWN : Direction.UP);
					return new Request(ActionType.MOVE, moveDir);
				}
			}
		}

		// eat
		if (energy < SharedConstants.SHEEP_ENERGY_HUNGRY) {
			BasicEntity prey = findNearestEntity(getPreyType(), SharedConstants.SHEEP_SIGHT_RANGE);
			if (prey != null && prey.isAlive()) {
				int dist = Math.abs(x - prey.getX()) + Math.abs(y - prey.getY());
				if (dist <= 1)
					return new Request(ActionType.INTERACT, prey.getId());
				else {
					int dx = prey.getX() - x;
					int dy = prey.getY() - y;
					Direction moveDir = (Math.abs(dx) >= Math.abs(dy))
						? ((dx > 0) ? Direction.RIGHT : Direction.LEFT)
						: ((dy > 0) ? Direction.DOWN : Direction.UP);
					return new Request(ActionType.MOVE, moveDir);
				}
			}
		}

		// move randomly
		Direction[] dirs = Direction.values();
		Direction randomDir = dirs[(int)(Math.random() * dirs.length)];
		return new Request(ActionType.MOVE, randomDir);
	}

	@Override
	public void transitionState(boolean actionSuccess) {
		((SheepState) currentState).toggle();
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
	public int getMovementSpeed() {
		return SharedConstants.SHEEP_MOVEMENT_SPEED;
	}

	@Override
	public BasicEntity spawnOffspring() {
		return new SheepEntity(grid, x, y, SharedConstants.SHEEP_ENERGY_DEFAULT);
	}

	private static class SheepState implements State {
		private boolean interact = true;
		public void toggle() {
			interact = !interact;
		}
	}
}
