package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import it.unibo.pss.common.SharedConstants;

public class PreyEntity extends AnimalEntity {

	/**
	 * Constructs a PreyEntity.
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public PreyEntity(World grid, int x, int y) {
		super(grid, x, y, SharedConstants.PREY_SPEED);
	}

	@Override
	public void update() {
		AnimalEntity predator = findNearestTarget(PredatorEntity.class) instanceof AnimalEntity ? (AnimalEntity) findNearestTarget(PredatorEntity.class) : null;
		if(predator != null && !(state instanceof FleeingState)) {
			setState(new FleeingState(predator));
		}
		super.update();
	}

	private void fleeFrom(AnimalEntity predator) {
		int dx = this.x - predator.getX();
		int dy = this.y - predator.getY();
		int fleeStepX = (dx == 0 ? 0 : Integer.signum(dx) * Math.min(speed, Math.abs(dx)));
		int fleeStepY = (dy == 0 ? 0 : Integer.signum(dy) * Math.min(speed, Math.abs(dy)));
		int newX = this.x + fleeStepX;
		int newY = this.y + fleeStepY;
		World.Tile targetTile = grid.getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newX, newY);
		} else {
			moveRandomly();
		}
	}

	@Override
	protected Class<? extends BasicEntity> getFoodType() {
		return PlantEntity.class;
	}

	@Override
	protected Class<? extends BasicEntity> getMateType() {
		return PreyEntity.class;
	}

	@Override
	protected void spawnOffspring() {
		new PreyEntity(grid, this.x, this.y);
	}

	/** FLEEING state specific to PreyEntity. */
	public static class FleeingState implements EntityState {
		private final AnimalEntity predator;
		public FleeingState(AnimalEntity predator) {
			this.predator = predator;
		}
		@Override
		public void execute(BasicEntity entity) {
			PreyEntity prey = (PreyEntity) entity;
			prey.fleeFrom(predator);
			prey.setState(new IdleState());
		}
		@Override
		public String getName() {
			return "FLEEING";
		}
	}
}
