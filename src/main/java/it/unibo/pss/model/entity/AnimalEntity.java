package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import java.util.Random;
import java.util.List;
import it.unibo.pss.common.SharedConstants;

/** An animal entity that seeks food when energy is low and mates when energy is sufficient. */
public class AnimalEntity extends BasicEntity {
	private static final Random random = new Random();
	private int energy;

	// Generalized target types for food and mating.
	private static final Class<? extends BasicEntity> FOOD_TYPE = PlantEntity.class;
	private static final Class<? extends BasicEntity> MATE_TYPE = AnimalEntity.class;

	public AnimalEntity(World grid, int x, int y) {
		super(grid, x, y);
		energy = SharedConstants.ANIMAL_INITIAL_ENERGY;
	}

	@Override
	public void update() {
		energy--;
		if (energy <= 0) {
			kill();
			return;
		}
		if (energy < SharedConstants.ANIMAL_ENERGY_THRESHOLD) {
			updateFoodBehavior();
		} else {
			updateMatingBehavior();
		}
	}

	/** Handles behavior when energy is low (seeking food). */
	private void updateFoodBehavior() {
		BasicEntity targetFood = findNearestTarget(FOOD_TYPE);
		if (targetFood != null) {
			moveTowards(targetFood.getX(), targetFood.getY());
			if (this.x == targetFood.getX() && this.y == targetFood.getY()) {
				targetFood.kill();
				energy += SharedConstants.ANIMAL_ENERGY_RESTORE;
			}
		} else {
			moveRandomly();
		}
	}

	/** Handles behavior when energy is sufficient (seeking mate). */
	private void updateMatingBehavior() {
		BasicEntity targetMate = findNearestTarget(MATE_TYPE);
		if (targetMate != null && targetMate != this) {
			moveTowards(targetMate.getX(), targetMate.getY());
			if (this.x == targetMate.getX() && this.y == targetMate.getY()) {
				AnimalEntity mate = (AnimalEntity) targetMate;
				if (this.energy >= SharedConstants.ANIMAL_REPRODUCTION_COST &&
					mate.energy >= SharedConstants.ANIMAL_REPRODUCTION_COST &&
					this.getId() < mate.getId()) {
					this.energy -= SharedConstants.ANIMAL_REPRODUCTION_COST;
					mate.energy -= SharedConstants.ANIMAL_REPRODUCTION_COST;
					new AnimalEntity(grid, this.x, this.y);
				}
			}
		} else {
			moveRandomly();
		}
	}

	/**
	 * Finds the nearest target entity of the specified type using Manhattan distance.
	 *
	 * @param type The class type of the target entity.
	 * @return The nearest target BasicEntity instance or null if none is found.
	 */
	private BasicEntity findNearestTarget(Class<? extends BasicEntity> type) {
		List<World.Tile> surroundingTiles = getSurroundingTiles(SharedConstants.ANIMAL_SEEK_RADIUS);
		BasicEntity target = null;
		int minDistance = Integer.MAX_VALUE;
		for (World.Tile tile : surroundingTiles) {
			for (BasicEntity entity : tile.getEntities()) {
				if (type.isInstance(entity) && entity.isAlive() && entity != this) {
					int distance = Math.abs(entity.getX() - this.x) + Math.abs(entity.getY() - this.y);
					if (distance < minDistance) {
						minDistance = distance;
						target = entity;
					}
				}
			}
		}
		return target;
	}

	/** Moves one step toward the target coordinates. */
	private void moveTowards(int targetX, int targetY) {
		int dx = targetX - this.x;
		int dy = targetY - this.y;
		int stepX = 0, stepY = 0;
		if (Math.abs(dx) > Math.abs(dy)) {
			stepX = Integer.signum(dx);
		} else if (Math.abs(dy) > 0) {
			stepY = Integer.signum(dy);
		}
		int newX = this.x + stepX;
		int newY = this.y + stepY;
		World.Tile targetTile = grid.getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newX, newY);
		} else {
			if (stepX != 0) {
				stepX = 0;
				stepY = Integer.signum(dy);
			} else if (stepY != 0) {
				stepY = 0;
				stepX = Integer.signum(dx);
			}
			newX = this.x + stepX;
			newY = this.y + stepY;
			targetTile = grid.getTile(newX, newY);
			if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
				moveTo(newX, newY);
			}
		}
	}

	/** Performs a random movement. */
	private void moveRandomly() {
		int[] dx = { -1, 1, 0, 0 };
		int[] dy = { 0, 0, -1, 1 };
		int index = random.nextInt(4);
		int newX = this.x + dx[index];
		int newY = this.y + dy[index];
		World.Tile targetTile = grid.getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newX, newY);
		}
	}

	@Override
	public void kill() {
		grid.getTile(x, y).removeEntity(this);
	}
}
