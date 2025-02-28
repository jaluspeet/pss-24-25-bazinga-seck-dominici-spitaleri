package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

/** Wolf entity */
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
		// Wolves have no natural predators.
		// Priority 1: If energy is high enough, seek mate
		if (energy >= SharedConstants.WOLF_ENERGY_BAZINGA) {
			BasicEntity mate = findNearestEntity(this.getClass(), SharedConstants.WOLF_SIGHT_RANGE);
			if (mate != null) {
				int dist = Math.abs(x - mate.getX()) + Math.abs(y - mate.getY());
				if (dist == 1)
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
		// Priority 2: If hungry, seek prey (sheep)
		if (energy < SharedConstants.WOLF_ENERGY_HUNGRY) {
			BasicEntity prey = findNearestEntity(getPreyType(), SharedConstants.WOLF_SIGHT_RANGE);
			if (prey != null) {
				int dist = Math.abs(x - prey.getX()) + Math.abs(y - prey.getY());
				if (dist == 1)
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
		// Default: move randomly
		Direction[] dirs = Direction.values();
		Direction randomDir = dirs[(int) (Math.random() * dirs.length)];
		return new Request(ActionType.MOVE, randomDir);
	}

	@Override
	public void transitionState(boolean actionSuccess) {
		((WolfState) currentState).toggle();
	}

	@Override
	public Class<? extends BasicEntity> getPreyType() {
		return SheepEntity.class;
	}

	@Override
	public Class<? extends BasicEntity> getPredatorType() {
		return null;
	}

	@Override
	public BasicEntity spawnOffspring() {
		return new WolfEntity(grid, x, y, SharedConstants.WOLF_ENERGY_DEFAULT);
	}

	private static class WolfState implements State {
		private boolean interact = true;
		public void toggle() {
			interact = !interact;
		}
	}
}
