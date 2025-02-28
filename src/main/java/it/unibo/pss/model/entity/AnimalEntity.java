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

	// Constructor for AnimalEntity
	public AnimalEntity(World grid, int x, int y, int speed) {
		super(grid, x, y);
		this.speed = speed;
		energy = SharedConstants.SHEEP_ENERGY_DEFAULT;
	}

	// Centralized energy management: subtract energy each update.
	@Override
	protected void updateState() {
		energy--;
		if (energy <= 0) {
			kill();
			return;
		}
		if (stateLock == 0) {
			setState(defaultState());
		}
	}

	// Returns the default state based on energy level
	protected BasicEntity.EntityState defaultState() {
		return energy < SharedConstants.SHEEP_ENERGY_HUNGRY ? new EatingState() : new MatingState();
	}

	// Finalizes the action by resetting the state to idle
	public void finalizeAction() {
		setState(new BasicEntity.IdleState());
	}

	// moveTo no longer subtracts energy
	@Override
	public void moveTo(int newX, int newY) {
		super.moveTo(newX, newY);
	}

	// Public getter for speed.
	public int getSpeed() {
		return speed;
	}

	// if both entities have sufficient energy deduct reproduction cost from both and spawn offspring.
	public boolean performMating(AnimalEntity mate) {
		if (this.getEnergy() >= SharedConstants.SHEEP_ENERGY_BAZINGA &&
				mate.getEnergy() >= SharedConstants.SHEEP_ENERGY_BAZINGA &&
				this.getId() < mate.getId()) {
			this.setEnergy(this.getEnergy() - SharedConstants.SHEEP_ENERGY_BAZINGA);
			mate.setEnergy(mate.getEnergy() - SharedConstants.SHEEP_ENERGY_BAZINGA);
			this.spawnOffspring();
			mate.spawnOffspring();
			return true;
				}
		return false;
	}

	// Finds the nearest target entity of the given type using Manhattan distance
	public BasicEntity findNearestTarget(Class<? extends BasicEntity> type) {
		int seekRadius = getSeekRadius();
		List<int[]> offsets = getOffsetsForRadius(seekRadius);
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

	/** Returns the seek radius for this animal. */
	public abstract int getSeekRadius();

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

	public int[] calculateNextPosition(int targetX, int targetY, boolean towards) {
		int dx = (towards ? (targetX - this.x) : (this.x - targetX));
		int dy = (towards ? (targetY - this.y) : (this.y - targetY));
		int stepX = 0, stepY = 0;
		if (Math.abs(dx) >= Math.abs(dy) && Math.abs(dx) > 0) {
			stepX = Integer.signum(dx) * Math.min(speed, Math.abs(dx));
		} else if (Math.abs(dy) > 0) {
			stepY = Integer.signum(dy) * Math.min(speed, Math.abs(dy));
		}
		return new int[] { this.x + stepX, this.y + stepY };
	}

	/* Moves one step toward the target coordinates. */
	public void moveTowards(int targetX, int targetY) {
		int[] newPos = calculateNextPosition(targetX, targetY, true);
		World.Tile targetTile = getGrid().getTile(newPos[0], newPos[1]);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			moveTo(newPos[0], newPos[1]);
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

	/** Public getter for energy. */
	public int getEnergy() {
		return energy;
	}

	/** Public setter for energy. */
	public void setEnergy(int energy) {
		this.energy = energy;
	}

	/* Overrides kill() to mark the animal as dead and remove it from the grid */
	@Override
	public void kill() {
		setState(new DeadState());
		getGrid().getTile(x, y).removeEntity(this);
	}

	/* Overrides isAlive() to return false if the animal is dead */
	@Override
	public boolean isAlive() {
		return !(state instanceof DeadState);
	}

	/* Abstract methods to define food type, mate type, and offspring creation. */
	public abstract Class<? extends BasicEntity> getFoodType();
	public abstract Class<? extends BasicEntity> getMateType();
	public abstract void spawnOffspring();
}
