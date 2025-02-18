package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import it.unibo.pss.common.SharedConstants;
import it.unibo.pss.model.entity.states.EatingState;
import it.unibo.pss.model.entity.states.MatingState;

public abstract class AnimalEntity extends BasicEntity {
	protected static final Random random = new Random();
	protected int energy;
	protected final int speed;
	protected static final Map<Integer, List<int[]>> offsetsCache = new HashMap<>();

	/* Constructor for AnimalEntity. */
	public AnimalEntity(World grid, int x, int y, int speed) {
		super(grid, x, y);
		this.speed = speed;
		energy = SharedConstants.ANIMAL_INITIAL_ENERGY;
	}

	/* Override updateState() to set the default behavior for AnimalEntity */
	@Override
	protected void updateState() {
		if (stateLock == 0) {
			if (energy < SharedConstants.ANIMAL_ENERGY_THRESHOLD) {
				setState(new EatingState());
			} else {
				setState(new MatingState());
			}
		}
	}

	/* Overrides moveTo to deduct movement energy */
	@Override
	public void moveTo(int newX, int newY) {
		super.moveTo(newX, newY);
		energy--;
	}

	/** Public getter for speed. */
	public int getSpeed() {
		return speed;
	}

	/* Finds the nearest target entity of the given type using Manhattan distance. */
	public BasicEntity findNearestTarget(Class<? extends BasicEntity> type) {
		List<int[]> offsets = getOffsetsForRadius(SharedConstants.ANIMAL_SEEK_RADIUS);
		BasicEntity target = null;
		int minDistance = Integer.MAX_VALUE;
		for (int[] offset : offsets) {
			int tileX = this.x + offset[0];
			int tileY = this.y + offset[1];
			World.Tile tile = getGrid().getTile(tileX, tileY);
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

	/* Returns a cached list of (dx,dy) offsets for a given radius. */
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

	/* Moves one step toward the target coordinates. */
	public void moveTowards(int targetX, int targetY) {
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
		World.Tile targetTile = getGrid().getTile(newX, newY);
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
			targetTile = getGrid().getTile(newX, newY);
			if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
				moveTo(newX, newY);
			}
		}
	}

	/* Performs a random movement scaled by speed. */
	public void moveRandomly() {
		int[] dxArr = { -1, 1, 0, 0 };
		int[] dyArr = { 0, 0, -1, 1 };
		int index = random.nextInt(4);
		int newX = this.x + dxArr[index] * speed;
		int newY = this.y + dyArr[index] * speed;
		World.Tile targetTile = getGrid().getTile(newX, newY);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newX, newY);
		}
	}

	/* Public getter for energy. */
	public int getEnergy() {
		return energy;
	}

	/* Public setter for energy. */
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/* Abstract methods to define food type, mate type, and offspring creation. */
	public abstract Class<? extends BasicEntity> getFoodType();
	public abstract Class<? extends BasicEntity> getMateType();
	public abstract void spawnOffspring();
}
