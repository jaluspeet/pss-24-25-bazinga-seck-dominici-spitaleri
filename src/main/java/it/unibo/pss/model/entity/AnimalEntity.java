package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import it.unibo.pss.common.SharedConstants;

/** A generalized animal entity with energy, food-seeking, and mating behavior. */
public abstract class AnimalEntity extends BasicEntity {
	protected static final Random random = new Random();
	protected int energy;
	protected final int speed;
	protected static final Map<Integer, List<int[]>> offsetsCache = new HashMap<>();

	/**
	 * Constructs an AnimalEntity.
	 * @param grid the world grid
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param speed the movement speed (in tiles per update)
	 */
	public AnimalEntity(World grid, int x, int y, int speed) {
		super(grid, x, y);
		this.speed = speed;
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

	/** Food behavior: search for target food and move toward it. */
	protected void updateFoodBehavior() {
		BasicEntity targetFood = findNearestTarget(getFoodType());
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

	/** Mating behavior: search for a mate and move toward it. */
	protected void updateMatingBehavior() {
		BasicEntity targetMate = findNearestTarget(getMateType());
		if (targetMate != null && targetMate != this) {
			moveTowards(targetMate.getX(), targetMate.getY());
			if (this.x == targetMate.getX() && this.y == targetMate.getY()) {
				AnimalEntity mate = (AnimalEntity) targetMate;
				if (this.energy >= SharedConstants.ANIMAL_REPRODUCTION_COST &&
				    mate.energy >= SharedConstants.ANIMAL_REPRODUCTION_COST &&
				    this.getId() < mate.getId()) {
					this.energy -= SharedConstants.ANIMAL_REPRODUCTION_COST;
					mate.energy -= SharedConstants.ANIMAL_REPRODUCTION_COST;
					spawnOffspring();
				}
			}
		} else {
			moveRandomly();
		}
	}

	/**
	 * Finds the nearest target entity of the specified type using Manhattan distance.
	 * Uses precomputed relative offsets for efficiency.
	 *
	 * @param type the class type of the target entity
	 * @return the nearest target entity or null if none found
	 */
	protected BasicEntity findNearestTarget(Class<? extends BasicEntity> type) {
		List<int[]> offsets = getOffsetsForRadius(SharedConstants.ANIMAL_SEEK_RADIUS);
		BasicEntity target = null;
		int minDistance = Integer.MAX_VALUE;
		for (int[] offset : offsets) {
			int tileX = this.x + offset[0];
			int tileY = this.y + offset[1];
			World.Tile tile = grid.getTile(tileX, tileY);
			if (tile == null || tile.getEntities().isEmpty())
				continue;
			int distance = Math.abs(offset[0]) + Math.abs(offset[1]);
			for (BasicEntity entity : tile.getEntities()) {
				if (type.isInstance(entity) && entity.isAlive() && entity != this) {
					if (distance < minDistance) {
						minDistance = distance;
						target = entity;
						if (distance == 1)
							return target;
					}
				}
			}
		}
		return target;
	}

	/**
	 * Returns a cached list of (dx,dy) offsets for a given radius.
	 * @param range the search radius
	 * @return list of offsets
	 */
	protected static List<int[]> getOffsetsForRadius(int range) {
		if (!offsetsCache.containsKey(range)) {
			List<int[]> offsets = new ArrayList<>();
			for (int dx = -range; dx <= range; dx++) {
				for (int dy = -range; dy <= range; dy++) {
					if (dx == 0 && dy == 0)
						continue;
					offsets.add(new int[] { dx, dy });
				}
			}
			offsetsCache.put(range, offsets);
		}
		return offsetsCache.get(range);
	}

	/**
	 * Moves one step toward the target coordinates.
	 * The step length is determined by this entity's speed.
	 */
	protected void moveTowards(int targetX, int targetY) {
		int dx = targetX - this.x;
		int dy = targetY - this.y;
		int stepX = 0, stepY = 0;
		if (Math.abs(dx) > Math.abs(dy)) {
			stepX = Integer.signum(dx) * Math.min(speed, Math.abs(dx));
		} else if (Math.abs(dy) > 0) {
			stepY = Integer.signum(dy) * Math.min(speed, Math.abs(dy));
		}
		int newX = this.x + stepX;
		int newY = this.y + stepY;
		World.Tile targetTile = grid.getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newX, newY);
		} else {
			if (stepX != 0) {
				stepX = 0;
				stepY = Integer.signum(dy) * Math.min(speed, Math.abs(dy));
			} else if (stepY != 0) {
				stepY = 0;
				stepX = Integer.signum(dx) * Math.min(speed, Math.abs(dx));
			}
			newX = this.x + stepX;
			newY = this.y + stepY;
			targetTile = grid.getTile(newX, newY);
			if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
				moveTo(newX, newY);
			}
		}
	}

	/**
	 * Performs a random movement scaled by the entity's speed.
	 */
	protected void moveRandomly() {
		int[] dxArr = { -1, 1, 0, 0 };
		int[] dyArr = { 0, 0, -1, 1 };
		int index = random.nextInt(4);
		int newX = this.x + dxArr[index] * speed;
		int newY = this.y + dyArr[index] * speed;
		World.Tile targetTile = grid.getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newX, newY);
		}
	}

	@Override
	public void kill() {
		grid.getTile(x, y).removeEntity(this);
	}

	// Abstract methods to define food type, mate type, and offspring creation.
	protected abstract Class<? extends BasicEntity> getFoodType();
	protected abstract Class<? extends BasicEntity> getMateType();
	protected abstract void spawnOffspring();
}
