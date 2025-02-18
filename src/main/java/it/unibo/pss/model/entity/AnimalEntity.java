package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import java.util.Random;
import java.util.List;
import it.unibo.pss.common.SharedConstants;

/** An animal entity that moves randomly on update. */
public class AnimalEntity extends BasicEntity {
	private static final Random random = new Random();

	public AnimalEntity(World grid, int x, int y) {
		super(grid, x, y);
	}

	@Override
	public void update() {
		List<World.Tile> surroundingTiles = getSurroundingTiles(SharedConstants.ANIMAL_SEEK_RADIUS);
		PlantEntity targetPlant = null;
		int minDistance = Integer.MAX_VALUE;
		// Find the nearest alive plant using Manhattan distance
		for (World.Tile tile : surroundingTiles) {
			for (BasicEntity entity : tile.getEntities()) {
				if (entity instanceof PlantEntity && entity.isAlive()) {
					int distance = Math.abs(entity.getX() - this.x) + Math.abs(entity.getY() - this.y);
					if (distance < minDistance) {
						minDistance = distance;
						targetPlant = (PlantEntity) entity;
					}
				}
			}
		}
		if (targetPlant != null) {
			moveTowards(targetPlant.getX(), targetPlant.getY());
			if (this.x == targetPlant.getX() && this.y == targetPlant.getY()) {
				targetPlant.kill();
			}
		} else {
			moveRandomly();
		}
	}

	/** Moves one step toward the target coordinates. */
	private void moveTowards(int targetX, int targetY) {
		int dx = targetX - this.x;
		int dy = targetY - this.y;
		int stepX = 0, stepY = 0;
		// Prioritize the axis with the larger distance
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
			// Try the other axis if the first move is invalid
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

	/** Retains the previous random movement logic. */
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
