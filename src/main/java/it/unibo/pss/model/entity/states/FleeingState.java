package it.unibo.pss.model.entity.states;

import it.unibo.pss.model.entity.AnimalEntity;
import it.unibo.pss.model.entity.BasicEntity;
import it.unibo.pss.model.world.World;

public class FleeingState implements BasicEntity.EntityState {
	private final AnimalEntity predator;

	public FleeingState(AnimalEntity predator) {
		this.predator = predator;
	}

	@Override
	public void execute(BasicEntity entity) {
		AnimalEntity prey = (AnimalEntity) entity;
		int[] newPos = prey.calculateNextPosition(predator.getX(), predator.getY(), false);
		World.Tile targetTile = prey.getGrid().getTile(newPos[0], newPos[1]);
		if (targetTile != null && targetTile.getType() == World.Tile.TileType.LAND) {
			prey.moveTo(newPos[0], newPos[1]);
		} else {
			prey.moveRandomly();
		}
	}

	@Override
	public String getName() {
		return "FLEEING";
	}
}
