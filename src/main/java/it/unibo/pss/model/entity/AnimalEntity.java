package it.unibo.pss.model.entity;

import it.unibo.pss.model.world.World;
import java.util.Random;

/** An animal entity that moves randomly on update. */
public class AnimalEntity extends BasicEntity {
	private static final Random random = new Random();
	
	public AnimalEntity(World grid, int x, int y) {
		super(grid, x, y);
	}
	
	@Override
	public void update() {
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
}
