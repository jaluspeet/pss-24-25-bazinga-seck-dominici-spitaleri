package it.unibo.pss.model.entity;

import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.world.World;

/** Sheep entity */
public class SheepEntity extends BasicEntity {
	public SheepEntity(World grid, int x, int y, int initialEnergy) {
		super(grid, x, y, initialEnergy);
	}

	@Override
	protected State initialState() {
		// Simple FSM state; details can be expanded if needed
		return new SheepState();
	}

	@Override
	public Request getNextRequest() {
		// Priority 1: Flee from predator (wolf)
		BasicEntity predator = findNearestEntity(getPredatorType(), SharedConstants.SHEEP_SIGHT_RANGE);
		if (predator != null) {
			// Compute opposite direction
			int dx = x - predator.getX();
			int dy = y - predator.getY();
			Direction fleeDir;
			if (Math.abs(dx) >= Math.abs(dy))
				fleeDir = (dx >= 0) ? Direction.RIGHT : Direction.LEFT;
			else
				fleeDir = (dy >= 0) ? Direction.DOWN : Direction.UP;
			return new Request(ActionType.MOVE, fleeDir);
		}
		// Priority 2: If energy is high enough for reproduction, seek mate
		if (energy >= SharedConstants.SHEEP_ENERGY_BAZINGA) {
			BasicEntity mate = findNearestEntity(this.getClass(), SharedConstants.SHEEP_SIGHT_RANGE);
			if (mate != null) {
				int dist = Math.abs(x - mate.getX()) + Math.abs(y - mate.getY());
				if (dist == 1)
					return new Request(ActionType.INTERACT, mate.getId());
				else {
					// Move towards mate
					int dx = mate.getX() - x;
					int dy = mate.getY() - y;
					Direction moveDir = (Math.abs(dx) >= Math.abs(dy))
						? ((dx > 0) ? Direction.RIGHT : Direction.LEFT)
						: ((dy > 0) ? Direction.DOWN : Direction.UP);
					return new Request(ActionType.MOVE, moveDir);
				}
			}
		}
		// Priority 3: If hungry, seek prey (plant)
		if (energy < SharedConstants.SHEEP_ENERGY_HUNGRY) {
			BasicEntity prey = findNearestEntity(getPreyType(), SharedConstants.SHEEP_SIGHT_RANGE);
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
