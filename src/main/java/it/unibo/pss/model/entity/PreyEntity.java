package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import java.util.List;
import it.unibo.pss.common.SharedConstants;

/** A prey entity that uses PlantEntity as food, reproduces with other PreyEntity, and flees from predators. */
public class PreyEntity extends AnimalEntity {

	public PreyEntity(World grid, int x, int y) {
		// Use a speed specific to prey.
		super(grid, x, y, SharedConstants.PREY_SPEED);
	}

	@Override
	public void update() {
		AnimalEntity predator = findNearestPredator();
		if (predator != null) {
			fleeFrom(predator);
			return;
		}
		super.update();
	}

	/**
	 * Searches for the nearest predator.
	 * @return the nearest PredatorEntity, or null if none is found.
	 */
	private AnimalEntity findNearestPredator() {
		List<int[]> offsets = getOffsetsForRadius(SharedConstants.ANIMAL_SEEK_RADIUS);
		AnimalEntity predator = null;
		int minDistance = Integer.MAX_VALUE;
		for (int[] offset : offsets) {
			int tileX = this.x + offset[0];
			int tileY = this.y + offset[1];
			World.Tile tile = grid.getTile(tileX, tileY);
			if (tile == null || tile.getEntities().isEmpty())
				continue;
			int distance = Math.abs(offset[0]) + Math.abs(offset[1]);
			for (BasicEntity entity : tile.getEntities()) {
				if (entity instanceof PredatorEntity && entity.isAlive()) {
					if (distance < minDistance) {
						minDistance = distance;
						predator = (AnimalEntity) entity;
						if (distance == 1)
							return predator;
					}
				}
			}
		}
		return predator;
	}

	/**
	 * Flees from the given predator by moving in the opposite direction.
	 * @param predator the predator to flee from.
	 */
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
}
